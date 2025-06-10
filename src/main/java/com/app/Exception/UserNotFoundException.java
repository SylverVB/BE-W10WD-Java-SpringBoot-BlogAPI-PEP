package com.app.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message); // Passing the message to the superclass constructor
    }
}