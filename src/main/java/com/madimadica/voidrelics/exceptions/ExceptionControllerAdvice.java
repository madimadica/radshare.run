package com.madimadica.voidrelics.exceptions;

import com.madimadica.utils.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class ExceptionControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler
    public ResponseEntity<?> handleApiError(ApiError error) {
        Map<String, Object> errorJson = Maps.ofMutable(
                "timestamp", Instant.now(),
                "status", error.status(),
                "message", error.message()
        );
        errorJson.putAll(error.details());
        LOGGER.warn("API Error {}: {} {}", error.status(), error.message(), error.details());
        return ResponseEntity.status(error.status()).body(errorJson);
    }

}
