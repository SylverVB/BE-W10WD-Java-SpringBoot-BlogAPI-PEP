package com.app.Exception;

public class LoginException extends IllegalArgumentException {
    public LoginException(String message) {
        super(message);
    }
}