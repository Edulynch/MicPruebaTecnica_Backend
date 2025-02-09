package com.blautech.pruebaTecnica.demo.api.users.exception;

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
    }
}
