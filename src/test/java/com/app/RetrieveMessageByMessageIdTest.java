package com.app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.app.Entity.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This test class verifies the behavior of the GET /messages/{message_id} endpoint
 * in the Social Media API. It checks whether a message can be successfully retrieved
 * by its ID and how the system handles a request for a non-existent message.
 *
 * The test environment is reset before each test by restarting the application context
 * to ensure isolated and consistent results. It uses HttpClient to simulate HTTP requests
 * and ObjectMapper to deserialize the JSON response into Message objects.
 *
 * Scenarios covered:
 * - Successfully retrieving a message by a valid ID
 * - Handling the case when the message ID does not exist
 */
public class RetrieveMessageByMessageIdTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Sets up the test environment before each test.
     * 
     * Initializes a new HttpClient and ObjectMapper, and starts the Spring Boot
     * application to allow HTTP requests to be made against a local server.
     * 
     * @throws InterruptedException if the thread sleep is interrupted
     */
    @BeforeEach
    public void setUp() throws InterruptedException {
        webClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        String[] args = new String[] {};
        app = SpringApplication.run(SocialMediaApp.class, args);
        Thread.sleep(500);
    }

    /**
     * Cleans up the test environment after each test.
     * 
     * Stops the Spring Boot application and adds a small delay to ensure proper shutdown.
     * 
     * @throws InterruptedException if the thread sleep is interrupted
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Sends a GET request to /messages/{message_id} where the message exists.
     * 
     * Verifies that the API returns HTTP 200 and the expected JSON representation of the message.
     * 
     * Expected Response:
     *   - Status Code: 200
     *   - Response Body: JSON representation of a Message object
     *
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void getMessageGivenMessageIdMessageFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        Message expectedResult = new Message(9999, 9999, "test message 1", 1669947792L);
        Message actualResult = objectMapper.readValue(response.body().toString(), Message.class);
        Assertions.assertEquals(expectedResult, actualResult, "Expected="+expectedResult + ", Actual="+actualResult);
    }

    /**
     * Sends a GET request to /messages/{message_id} where the message does not exist.
     * 
     * Verifies that the API still returns HTTP 200, but the response body is empty.
     * 
     * Expected Response:
     *   - Status Code: 200
     *   - Response Body: Empty
     *
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void getMessageGivenMessageIdMessageNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/100"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        Assertions.assertTrue(response.body().toString().isEmpty(), "Expected Empty Result, but Result was not Empty");
    }
}