package ru.practicum.shareit.gateway.config;

import ru.practicum.shareit.gateway.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(HttpClientErrorException e) {
        return buildErrorResponse(e.getStatusCode(), e.getResponseBodyAsString());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerError(HttpServerErrorException e) {
        return buildErrorResponse(e.getStatusCode(), e.getResponseBodyAsString());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatusCode statusCode, String message) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        ErrorResponse errorResponse = new ErrorResponse(
                status.getReasonPhrase(),
                message,
                status.value()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}