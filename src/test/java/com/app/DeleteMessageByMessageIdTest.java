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
 * Integration tests for verifying DELETE operations on the /messages/{id} endpoint.
 *
 * These tests ensure that messages can be deleted by their message ID using HTTP DELETE requests.
 * The tests verify both successful deletion of existing messages and the expected behavior
 * when attempting to delete a message that does not exist.
 *
 * Each test spins up a Spring Boot context to simulate a local server for HTTP communication.
 */
public class DeleteMessageByMessageIdTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Sets up the test environment before each test runs.
     * 
     * Initializes a new HttpClient and ObjectMapper, and starts the Spring Boot application.
     * A short delay is introduced to ensure the server is fully ready to accept requests.
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
     * Introduces a delay for clean shutdown and then stops the Spring Boot application context.
     * 
     * @throws InterruptedException if thread sleep is interrupted
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Sends an HTTP DELETE request to /messages/9999 where the message is expected to exist.
     * 
     * Verifies that the server returns a 200 OK status and the response body contains 1,
     * indicating that one row (the message) was successfully deleted.
     * 
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void deleteMessageGivenMessageIdMessageFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/9999"))
                .DELETE()
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        Integer actualResult = objectMapper.readValue(response.body().toString(), Integer.class);
        Assertions.assertTrue(actualResult.equals(1), "Expected to modify 1 row, but actually modified " + actualResult + " rows.");
    }

    /**
     * Sends an HTTP DELETE request to /messages/100 where the message is not expected to exist.
     * 
     * Verifies that the server still returns a 200 OK status and the response body is empty,
     * indicating that no rows were affected.
     * 
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void deleteMessageGivenMessageIdMessageNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages/100"))
                .DELETE()
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        String actualResult = response.body().toString();
        Assertions.assertTrue(actualResult.equals(""), "Expected empty response body, but actually " + actualResult + ".");
    }
}