package com.app.Exception;

public class MessageTooLongException extends RuntimeException {
    public MessageTooLongException(String message) {
        super(message);
    }
}