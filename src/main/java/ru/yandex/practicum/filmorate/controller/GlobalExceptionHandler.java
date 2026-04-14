package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        String jsonError = String.format("{\"error\":\"ValidationError\",\"message\":\"%s\"}",
                ex.getMessage().replace("\"", "\\\""));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(jsonError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        String jsonError = String.format("{\"error\":\"NotFoundError\",\"message\":\"%s\"}",
                ex.getMessage().replace("\"", "\\\""));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(jsonError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"InternalError\",\"message\":\"Произошла внутренняя ошибка сервера\"}");
    }
}
