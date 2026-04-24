package com.smart.smart_backend.domain.exception;

public class EntryNotFoundException extends RuntimeException {
    public EntryNotFoundException(Long entryId) {
        super(
                "Entry with id=%d not found.".formatted(entryId)
        );
    }
}
