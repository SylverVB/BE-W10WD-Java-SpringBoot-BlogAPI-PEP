package com.app.Controller;

import com.app.Entity.Account;
import com.app.Entity.Message;
import com.app.Service.AccountService;
import com.app.Service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for handling account and message-related operations.
 * Provides endpoints for user registration, authentication, and message management.
 */
@RestController
public class SocialMediaController {
    private final AccountService accountService;
    private final MessageService messageService;

    /**
     * Constructor for SocialMediaController, injecting required services.
     *
     * @param accountService Service handling account operations.
     * @param messageService Service handling message operations.
     */
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
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

    // ========================== Message-related endpoints ==========================

    /**
     * Creates a new message.
     *
     * @param message The message object containing sender, receiver, and content.
     * @return A ResponseEntity containing the created account and the HTTP status.
     * 
     * Note: MessageBlankTextException, MessageTooLongException, and UserNotFoundException are handled globally by GlobalExceptionHandler.
     */
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return ResponseEntity.ok(messageService.createMessage(message));
    }

    /**
     * Retrieves all messages.
     *
     * @return A ResponseEntity containing a list of all messages and the HTTP status.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    /**
     * Retrieves a message by its ID.
     *
     * @param messageId The ID of the message to retrieve.
     * @return A ResponseEntity containing the requested message and the HTTP status.
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Message message = messageService.getMessageById(messageId);
        return ResponseEntity.ok(message);
    }

    /**
     * Updates the text of an existing message by ID.
     *
     * @param messageId The ID of the message to update.
     * @param message   The new message object containing the updated content.
     * @return A ResponseEntity containing the number of updated rows and the HTTP status.
     * 
     * Note: MessageBlankTextException, MessageTooLongException, and MessageNotFoundException are handled globally by GlobalExceptionHandler.
     */
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable Integer messageId, @RequestBody Message message) {
        // Calling the service layer to update the message
        int rowsUpdated = messageService.updateMessage(messageId, message);
    
        // If successful, returning the updated message count (1 expected)
        return ResponseEntity.ok(rowsUpdated);
    }

    /**
     * Deletes a message by its ID.
     *
     * @param messageId The ID of the message to delete.
     * @return A ResponseEntity containing the deletion status and the HTTP status.
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) {
        boolean messageFound = messageService.deleteMessage(messageId);

        // If the message was found and deleted, return 200 OK with "1" (modified rows) in the response body.
        if (messageFound) {
            return ResponseEntity.ok(1);
        }
        
        // If the message was not found, return 200 OK with an empty body
        return ResponseEntity.ok().build();
    } 

    /**
     * Retrieves all messages sent by a specific user.
     *
     * @param accountId The ID of the user whose messages are to be retrieved.
     * @return A ResponseEntity containing a list of messages from the specified user and the HTTP status.
     * 
     * Note: Returns an empty list if the user has no messages.
     */
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByUser(accountId);
    
        // Returning 200, even if the list is empty
        return ResponseEntity.ok(messages);
    }
}