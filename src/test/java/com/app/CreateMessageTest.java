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
 * Integration tests for the message creation endpoint of the application.
 */
public class CreateMessageTest {	
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Sets up the test environment before each test by starting the Spring Boot application
     * and initializing HTTP client and JSON object mapper.
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
     * Shuts down the Spring Boot application after each test to ensure a clean environment.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }

    /**
     * Sends an HTTP POST request to /messages with valid data.
     *
     * Expected outcome:
     * - Status Code: 200
     * - Response Body: JSON representation of the created message
     *
     * @throws IOException if request fails
     * @throws InterruptedException if the request thread is interrupted
     */
    @Test
    public void createMessageSuccessful() throws IOException, InterruptedException {
    	String json = "{\"postedBy\":9999,\"messageText\": \"hello message\",\"timePostedEpoch\": 1669947792}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        ObjectMapper om = new ObjectMapper();
        Message expectedResult = new Message(1, 9999, "hello message", Long.valueOf(1669947792));
        Message actualResult = om.readValue(response.body().toString(), Message.class);
        Assertions.assertEquals(expectedResult, actualResult, "Expected="+expectedResult + ", Actual="+actualResult);
    }
    
    /**
     * Sends an HTTP POST request to /messages with an empty message text.
     *
     * Expected outcome:
     * - Status Code: 400
     *
     * @throws IOException if request fails
     * @throws InterruptedException if the request thread is interrupted
     */
    @Test
    public void createMessageMessageTextBlank() throws IOException, InterruptedException {
    	String json = "{\"postedBy\":9999,\"messageText\": \"\",\"timePostedEpoch\": 1669947792}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
    }

    /**
     * Sends an HTTP POST request to /messages with a message text exceeding 254 characters.
     *
     * Expected outcome:
     * - Status Code: 400
     *
     * @throws IOException if request fails
     * @throws InterruptedException if the request thread is interrupted
     */
    @Test
    public void createMessageMessageGreaterThan255() throws IOException, InterruptedException {
    	String json = "{\"postedBy\":9999,"
    			+ "\"messageText\": \"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\","
    			+ "\"timePostedEpoch\": 1669947792}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
    }

    /**
     * Sends an HTTP POST request to /messages with a non-existent user ID.
     *
     * Expected outcome:
     * - Status Code: 400
     *
     * @throws IOException if request fails
     * @throws InterruptedException if the request thread is interrupted
     */
    @Test
    public void createMessageUserNotInDb() throws IOException, InterruptedException {
    	String json = "{\"postedBy\":5050,\"messageText\": \"hello message\",\"timePostedEpoch\": 1669947792}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
    }
}