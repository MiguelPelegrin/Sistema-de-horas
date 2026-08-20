package com.cronos.cronosystem.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Usuário ou senha inválidos");
    }
}
