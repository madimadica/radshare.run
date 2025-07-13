package com.madimadica.voidrelics.exceptions;

import com.madimadica.utils.Maps;

import java.time.Instant;
import java.util.Map;

public class ApiError extends RuntimeException {
    private final int status;
    private final String message;
    private final Map<String, Object> details;

    public ApiError(int status, String message) {
        super("Error " + status + ": " + message);
        this.status = status;
        this.message = message;
        this.details = Map.of();
    }

    public ApiError(int status, String message, Map<String, Object> details) {
        super("Error " + status + ": " + message);
        this.status = status;
        this.message = message;
        this.details = Map.copyOf(details);
    }

    public int status() {
        return status;
    }

    public String message() {
        return message;
    }

    public Map<String, Object> details() {
        return details;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> errorJson = Maps.ofMutable(
                "timestamp", Instant.now(),
                "status", status(),
                "message", message()
        );
        errorJson.putAll(details());
        return errorJson;
    }
}
