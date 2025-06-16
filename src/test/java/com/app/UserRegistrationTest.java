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

public class UserRegistrationTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Initializes the test environment before each test.
     *
     * - Starts the Spring Boot application.
     * - Creates a new HttpClient for sending HTTP requests.
     * - Initializes a new ObjectMapper for JSON serialization and deserialization.
     * - Waits briefly to ensure the server is fully up and running before sending requests.
     *
     * @throws InterruptedException if the thread sleep is interrupted.
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
     * - Waits briefly to allow any pending operations to complete.
     * - Gracefully shuts down the Spring Boot application.
     *
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Tests user registration with a unique username.
     *
     * Sends an HTTP POST request to http://localhost:8080/register with a JSON payload
     * representing a new user. Expects the server to respond with:
     *   - Status Code: 200 OK
     *   - Response Body: JSON representation of the registered user
     *
     * @throws IOException if an I/O error occurs during the request.
     * @throws InterruptedException if the thread is interrupted while waiting for the response.
     */
    @Test
    public void registerUserSuccessful() throws IOException, InterruptedException {
        String json = "{\"username\":\"user\",\"password\":\"password\"}";
    	HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200- Actual Code was: " + status);
    }

    /**
     * Tests registration of a user with a duplicate username.
     *
     * Sends two consecutive HTTP POST requests to http://localhost:8080/register
     * with the same JSON payload. Expects:
     *   - First Request: 200 OK
     *   - Second Request: 409 Conflict due to duplicate username
     *
     * @throws IOException if an I/O error occurs during the requests.
     * @throws InterruptedException if the thread is interrupted while waiting for the responses.
     */
    @Test
    public void registerUserDuplicateUsername() throws IOException, InterruptedException {
    	String json = "{\"username\":\"user\",\"password\":\"password\"}";
    	HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response1 = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status1 = response1.statusCode();
        int status2 = response2.statusCode();
        Assertions.assertEquals(200, status1, "Expected Status Code 200 - Actual Code was: " + status1);
        Assertions.assertEquals(409, status2, "Expected Status Code 409 - Actual Code was: " + status2);
    }
}