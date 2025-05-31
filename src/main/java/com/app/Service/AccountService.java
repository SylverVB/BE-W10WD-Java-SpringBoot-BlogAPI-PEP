package com.app.Service;

import com.app.Entity.Account;
import com.app.Exception.DuplicateUsernameException;
import com.app.Exception.RegistrationException;
import com.app.Exception.LoginException;
import com.app.Repository.AccountRepository;
import org.springframework.stereotype.Service;

/**
 * AccountService class responsible for handling account-related operations
 * such as registration and authentication.
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    /**
     * Constructs an AccountService with the provided repository.
     * 
     * @param accountRepository The repository used for account persistence operations.
     * 
     * Note: Spring automatically injects the AccountRepository (Constructor injection).
     * The @Autowired annotation is not required when there is only one constructor.
     */
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Registers a new user account after validating the username and password.
     * 
     * @param account The account object containing user details.
     * @return The registered Account entity.
     * @throws RegistrationException If the username is blank or the password is too short.
     * @throws DuplicateUsernameException If an account with the given username already exists.
     * 
     * Note: IllegalArgumentException and DuplicateUsernameException are handled by GlobalExceptionHandler.
     */
    public Account registerAccount(Account account) {
        // Validating username
        String username = account.getUsername();
        if (username == null || username.isBlank()) {
            throw new RegistrationException(""); // "Username cannot be blank."
        }

        // Validating password
        String password = account.getPassword();
        if (password == null || password.length() < 4) {
            throw new RegistrationException(""); // "Password must be at least 4 characters long."
        }

        // Checking if the username already exists
        if (accountExists(username)) {
            throw new DuplicateUsernameException(""); // "Account with this username already exists."
        }

        // If validations pass, persist the account and return the saved object
        return accountRepository.save(account);
    }

    /**
     * Checks if an account exists by its username.
     * 
     * @param username The username to check for in the database.
     * @return True if an account exists with the given username, false otherwise.
     */
    public boolean accountExists(String username) {
        return accountRepository.findAccountByUsername(username).isPresent();
    }

    /**
     * Authenticates a user by verifying the provided username and password.
     * 
     * @param username The username provided by the user.
     * @param password The password provided by the user.
     * @return The authenticated Account object if login is successful.
     * @throws LoginException If the username doesn't exist or password is incorrect.
     * 
     * Note: LoginException is handled by GlobalExceptionHandler.
     */
    public Account login(String username, String password) throws LoginException {
        // Step 1: Checking if the account exists by username, otherwise throw AccountNotFoundException
        Account account = accountRepository.findAccountByUsername(username)
            .orElseThrow(() -> new LoginException("")); // "Account with the given username does not exist."

        // Step 2: Checking if the password matches
        if (!account.getPassword().equals(password)) {
            // Invalid password, throwing exception
            throw new LoginException(""); // "Incorrect password."
        }

        // Returning the authenticated account if successful
        return account;
    }
}