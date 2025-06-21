package com.app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.app.Entity.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for retrieving messages posted by a specific user.
 *
 * This test class verifies the behavior of the GET /accounts/{accountId}/messages endpoint 
 * by checking the server response when:
 * - Messages exist for the given user ID.
 * - No messages exist for the given user ID.
 *
 * The tests simulate HTTP requests and assert the correctness of HTTP status codes 
 * and response payloads using JUnit, HttpClient, and Jackson ObjectMapper.
 *
 * The Spring Boot application is started and stopped before and after each test to ensure
 * isolation and reliability of test outcomes.
 */
public class RetrieveAllMessagesForUserTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Sets up the test environment before each test.
     * 
     * Starts the Spring Boot application, initializes HttpClient and ObjectMapper, 
     * and waits briefly to ensure the app is ready to handle requests.
     * 
     * @throws InterruptedException if thread sleep is interrupted
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
     * Tears down the test environment after each test.
     * 
     * Shuts down the Spring Boot application and pauses briefly to ensure clean shutdown.
     * 
     * @throws InterruptedException if thread sleep is interrupted
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Sends an HTTP GET request to /accounts/9999/messages where messages exist for the user.
     * 
     * Expected Response:
     * - Status Code: 200
     * - Response Body: JSON array representing a list of Message objects
     */
    @Test
    public void getAllMessagesFromUserMessageExists() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/accounts/9999/messages"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        List<Message> expectedResult = new ArrayList<Message>();
        expectedResult.add(new Message(9999, 9999, "test message 1", 1669947792L));
        List<Message> actualResult = objectMapper.readValue(response.body().toString(), new TypeReference<List<Message>>(){});
        Assertions.assertEquals(expectedResult, actualResult, "Expected="+expectedResult + ", Actual="+actualResult);
    }
    
    /**
     * Sends an HTTP GET request to /accounts/9998/messages where no messages exist for the user.
     * 
     * Expected Response:
     * - Status Code: 200
     * - Response Body: Empty JSON array ([])
     */
    @Test
    public void getAllMessagesFromUserNoMessagesFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/accounts/9998/messages"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        List<Message> actualResult = objectMapper.readValue(response.body().toString(), new TypeReference<List<Message>>(){});
        Assertions.assertTrue(actualResult.isEmpty(), "Expected Empty Result, but Result was not Empty");
    }
}