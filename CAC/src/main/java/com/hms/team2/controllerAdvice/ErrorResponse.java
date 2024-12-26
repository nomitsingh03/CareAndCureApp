package com.hms.team2.controllerAdvice;

import java.util.Map;

public class ErrorResponse {
    private String message;
    private Map<String, String> errors;

    public ErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ErrorResponse [message=" + message + ", errors=" + errors + "]";
    }

    
}
