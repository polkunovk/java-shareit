package ru.practicum.shareit.gateway.config;

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
    public ResponseEntity<Map<String, Object>> handleHttpClientError(HttpClientErrorException e) {
        return buildErrorResponse(e.getStatusCode(), e.getResponseBodyAsString());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpServerError(HttpServerErrorException e) {
        return buildErrorResponse(e.getStatusCode(), e.getResponseBodyAsString());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatusCode statusCode, String message) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());

        Map<String, Object> response = new HashMap<>();
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("status", status.value());

        return ResponseEntity.status(status).body(response);
    }
}

