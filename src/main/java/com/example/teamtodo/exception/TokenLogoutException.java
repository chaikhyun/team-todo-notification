package com.example.teamtodo.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenLogoutException extends RuntimeException {
    public TokenLogoutException(String message) {
        super(message);
    }
}

