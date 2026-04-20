package com.smart.smart_backend.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("El email " + email + " ya se encuentra registrado.");
    }
}
