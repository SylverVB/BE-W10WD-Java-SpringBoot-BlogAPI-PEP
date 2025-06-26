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

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for updating messages in the SocialMediaApp using HTTP PATCH requests.
 *
 * This test class verifies the API's behavior for updating message content by sending PATCH
 * requests to /messages/{message_id}. The test cases cover successful updates as well
 * as various failure scenarios such as nonexistent message IDs, empty message content, and content
 * exceeding allowed length.
 *
 * Each test runs in an isolated Spring Boot context with a fresh application state ensured by
 * restarting the app before and after each test method.
 */
public class UpdateMessageTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Sets up the test environment before each test.
     *
     * Initializes a new HTTP client and object mapper,
     * and launches the Spring Boot application to handle local requests.
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
     * Cleans up the test environment after each test.
     *
     * Stops the Spring Boot application and pauses briefly to
     * allow graceful shutdown.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Sends a PATCH request to update the message text of a valid message ID.
     *
     * Verifies that the response status is 200 and one row is modified.
     *
     * @throws IOException if an I/O error occurs during request/response
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void updateMessageSuccessful() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"text changed\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        Integer actualResult = objectMapper.readValue(response.body().toString(), Integer.class);
        Assertions.assertTrue(actualResult.equals(1), "Expected to modify 1 row, but actually modified " + actualResult + " rows.");
    }

    /**
     * Sends a PATCH request for a message ID that does not exist in the database.
     *
     * Verifies that the server returns status 400 indicating a bad request.
     *
     * @throws IOException if an I/O error occurs during request/response
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void updateMessageMessageNotFound() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"text changed\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
        		.uri(URI.create("http://localhost:8080/messages/5050"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
        System.out.println(response.body());
    }

    /**
     * Sends a PATCH request with an empty message string to an existing message.
     *
     * Verifies that the server returns status 400 indicating invalid input.
     *
     * @throws IOException if an I/O error occurs during request/response
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void updateMessageMessageStringEmpty() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
    }

    /**
     * Sends a PATCH request with message text exceeding 255 characters.
     *
     * Verifies that the server returns status 400 due to message length constraints.
     *
     * @throws IOException if an I/O error occurs during request/response
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void updateMessageMessageTooLong() throws IOException, InterruptedException {
    	String json = "{\"messageText\": \"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}";
        HttpRequest postMessageRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postMessageRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(400, status, "Expected Status Code 400 - Actual Code was: " + status);
    }
}