package com.smart.smart_backend.domain.exception;

public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportGenerationException(String message) {
        super(message);
    }
}