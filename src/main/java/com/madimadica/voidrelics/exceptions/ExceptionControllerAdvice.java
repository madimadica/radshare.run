package com.madimadica.voidrelics.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler
    public ResponseEntity<?> handleApiError(ApiError error) {
        var errorJson = error.toJson();
        LOGGER.warn("API Error {}: {} {}", error.status(), error.message(), error.details());
        return ResponseEntity.status(error.status()).body(errorJson);
    }

}
