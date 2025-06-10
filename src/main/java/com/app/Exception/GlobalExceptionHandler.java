package com.app.Exception;

import java.time.Instant;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle unexpected exceptions (catch-all)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e) {
        return "An unexpected error occurred."; // General fallback message
    }

    // Handle 404 Not Found errors when a request does not match any existing route
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Explicitly return 404
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NoHandlerFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", "The requested resource was not found");
        response.put("path", ex.getRequestURL());
    
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }    

    // ========================== Account-related exceptions ==========================

    // Handle Duplicate Username Exception during Registration
    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateUsernameException(DuplicateUsernameException e) {
        return e.getMessage(); // Return error message from the AccountService class
    }

    // Handle Registration-related exceptions (invalid data, username blank, or password too short)
    @ExceptionHandler(RegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400 Bad Request for registration failures
    public String handleRegistrationException(RegistrationException e) {
        return e.getMessage(); // Return error message from the AccountService class
    }

    // Handle Login failures (invalid username/password)
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401 Unauthorized for login failures
    public String handleLoginException(LoginException e) {
        return e.getMessage(); // Return error message from the AccountService class
    }

    // ========================== Message-related exceptions ==========================

    // Handle Message Blank Text Exception
    @ExceptionHandler(MessageBlankTextException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMessageBlankTextException(MessageBlankTextException e) {
        return e.getMessage(); // Return error message from the MessageService class
    }

    // Handle Message Blank Too Long Exception
    @ExceptionHandler(MessageTooLongException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMessageTooLongException(MessageTooLongException e) {
        return e.getMessage(); // Return error message from the MessageService class
    }

    // Handle Message Not Found Exception
    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMessageNotFoundException(MessageNotFoundException e) {
        return e.getMessage(); // Return error message from the MessageService class
    }

    // Handle User Not Found Exception
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUserNotFoundException(UserNotFoundException e) {
        return e.getMessage(); // Return message indicating the user doesn't exist
    }
}