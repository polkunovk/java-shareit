package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> throwableHandler(Throwable exception, WebRequest request) {
        logError("Ошибка", exception, request);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put(ERROR, "Ошибка сервера");
        body.put(MESSAGE, "Произошла ошибка");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> shareItExceptionHandler(ShareItException exception, WebRequest request) {
        logError("Ошибка", exception, request);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, exception.getStatus().value());
        body.put(ERROR, translateErrorCode(exception.getShareItExceptionCodes()));
        body.put(MESSAGE, exception.getMessage());
        return new ResponseEntity<>(body, exception.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> validationHandler(MethodArgumentNotValidException exception, WebRequest request) {
        logError("Ошибка", exception, request);

        String errorMessage = exception.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return String.format("Поле '%s': %s",
                                fieldError.getField(),
                                translateValidationMessage(fieldError.getDefaultMessage()));
                    }
                    return "Некорректные данные";
                })
                .collect(Collectors.joining("; "));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, "Ошибка ввода");
        body.put(MESSAGE, errorMessage);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private String translateErrorCode(ShareItExceptionCodes code) {
        return switch (code) {
            case USER_NOT_FOUND -> "Не найден пользователь";
            case ITEM_NOT_FOUND -> "Не найдена вещь";
            case BOOKING_NOT_FOUND -> "Не найдено бронирование";
            case ACCESS_DENIED -> "Нет доступа";
            case ITEM_NOT_AVAILABLE -> "Вещь недоступна";
            default -> "Ошибка";
        };
    }

    private String translateValidationMessage(String message) {
        if (message == null) return "Некорректное значение";
        return message
                .replace("must not be null", "обязательное поле")
                .replace("must not be blank", "обязательное поле")
                .replace("must be a future date", "укажите будущую дату")
                .replace("must be greater than 0", "введите число больше 0");
    }

    private void logError(String prefix, Throwable exception, WebRequest request) {
        System.err.printf("[Ошибка] %s: %s%n", LocalDateTime.now(), exception.getMessage());
    }
}