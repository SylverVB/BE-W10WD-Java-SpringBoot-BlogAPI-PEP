package com.app.Controller;

import com.app.Entity.Account;
import com.app.Service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling account operations.
 * Provides endpoints for user registration and authentication.
 */
@RestController
public class SocialMediaController {
    private final AccountService accountService;

    /**
     * Constructor for SocialMediaController, injecting required services.
     *
     * @param accountService Service handling account operations.
     */
    public SocialMediaController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ========================== Account-related endpoints ==========================

    /**
     * Registers a new account using data from the request body.
     * Validates the account data, creates the account, and responds with account details.
     * 
     * @param newAccount The account data (username and password) to be used for registration.
     * @return A ResponseEntity containing the registered account details and the HTTP status.
     * 
     * The account's password should not be included in the response for security reasons.
     * In case of failure (e.g., duplicate username or invalid data), appropriate HTTP status is returned.
     * 
     * Note: RegistrationException and DuplicateUsernameException are handled globally by GlobalExceptionHandler.
     */
    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account newAccount) {
        // Step 1: Attempt to register the new account using the service
        Account registeredAccount = accountService.registerAccount(newAccount);

        // Step 2: If registration is successful, return the account details with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(registeredAccount); // or return ResponseEntity.ok(registeredAccount);
        // return ResponseEntity.status(HttpStatus.CREATED).body(registeredAccount); // In RESTful API design, 201 Created is the more appropriate status code when successfully creating a new resource
    }

    /**
     * Authenticates a user by verifying the provided username and password.
     * 
     * @param existingAccount The account data (username and password) provided by the user for login.
     * @return A ResponseEntity containing the authenticated account details and the HTTP status.
     * 
     * Note: LoginException (invalid username or password) is handled globally by GlobalExceptionHandler.
     */
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account existingAccount) {
        // Step 1: Authenticating the account using the service
        Account authenticatedAccount = accountService.login(existingAccount.getUsername(), existingAccount.getPassword());

        // Step 2: If login is successful, returning the account details with 200 OK status
        return ResponseEntity.ok(authenticatedAccount); // return ResponseEntity.status(200).body(authenticatedAccount);
    }
}