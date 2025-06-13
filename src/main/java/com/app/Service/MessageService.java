package com.app.Service;

import com.app.Entity.Message;
import com.app.Repository.MessageRepository;
import com.app.Repository.AccountRepository;
import org.springframework.stereotype.Service;
import com.app.Exception.MessageBlankTextException;
import com.app.Exception.MessageTooLongException;
import com.app.Exception.MessageNotFoundException;
import com.app.Exception.UserNotFoundException;
import java.util.List;

@Service
public class MessageService {
    public final MessageRepository messageRepository;
    public final AccountRepository accountRepository;

    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Posts a new message after validating its content and the user posting it.
     *
     * @param message The Message object containing the message text, posted_by (account), and time posted.
     * @return The persisted Message object after being saved to the database.
     * @throws MessageBlankTextException If the message text is blank.
     * @throws MessageTooLongException If the message text is longer than the maximum allowed length of 255 characters.
     * @throws UserNotFoundException If the user posting the message does not exist.
     */
    public Message createMessage(Message message) {
        Integer postedBy = message.getPostedBy();
        String text = message.getMessageText();

        // Validating message_text to ensure it's not empty or too long
        if (text == null || text.trim().isBlank()) {
            throw new MessageBlankTextException(""); // "Message text cannot be blank."
        }
        if (text.length() > 255) {
            throw new MessageTooLongException(""); // "Message text must be under 255 characters."
        }

        // Validating postedBy to ensure the user exists in the system
        if (!accountRepository.findAccountByAccountId(postedBy).isPresent()) {
            throw new UserNotFoundException(""); // "User with ID " + postedBy + " does not exist."
        }

        // Persisting the message in the database
        return messageRepository.save(message);
    }

    /**
     * Retrieves all messages from the database.
     *
     * @return A list of all messages.
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * Retrieves a specific message by its unique ID.
     *
     * @param messageId The unique ID of the message to retrieve.
     * @return The Message object corresponding to the given ID, or null if not found.
     */
    public Message getMessageById(Integer messageId) {
        return messageRepository.findById(messageId).orElse(null); // Returns null if the message is not found
    }

    /**
     * Retrieves all messages associated with a specific user by account ID.
     *
     * @param accountId The unique ID of the account (user) whose messages are to be retrieved.
     * @return A list of messages for the specified account (user).
     */
    public List<Message> getMessagesByUser(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }

    /**
     * Updates an existing message with new content.
     *
     * @param messageId The ID of the message to update.
     * @param message The message object containing the new content.
     * @return The number of rows updated (expected to be 1).
     * @throws MessageBlankTextException If the new message text is blank.
     * @throws MessageTooLongException If the new message text is longer than the maximum allowed length of 255 characters.
     * @throws MessageNotFoundException If the message with the given ID does not exist.
     */
    public int updateMessage(Integer messageId, Message message) {
        // Extracting the messageText from the message object
        String newText = message.getMessageText();
    
        // Validating the input message text in the service layer
        if (newText == null || newText.trim().isBlank()) {
            throw new MessageBlankTextException(""); // "Message text cannot be blank."
        }
        if (newText.length() > 255) {
            throw new MessageTooLongException(""); // "Message text must be under 255 characters."
        }
    
        // Performing the update using the repository method
        int rowsUpdated = messageRepository.updateMessageText(messageId, newText);
    
        // Handling the case where no rows were updated (message not found)
        if (rowsUpdated == 0) {
            throw new MessageNotFoundException(""); // "Message not found with ID: " + messageId
        }
    
        // Returning the number of updated rows (1 expected)
        return rowsUpdated;
    }

    /**
     * Deletes a message from the 'message' table by its ID.
     *
     * @param messageId The unique ID of the message to delete.
     * @return true if the message was deleted, false if the message was not found or deletion failed.
     */
    public boolean deleteMessage(Integer messageId) {
        if (!messageRepository.existsById(messageId)) {
            return false; // Returning false if the message was not found
        }
        messageRepository.deleteById(messageId);
        return true;  // Returning true if the message was deleted
    } 
}