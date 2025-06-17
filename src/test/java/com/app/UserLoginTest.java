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

import com.app.Entity.Account;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for the user login functionality of the SocialMediaApp.
 *
 * These tests simulate HTTP requests to the login endpoint and assert expected
 * behavior for valid and invalid credentials.
 */
public class UserLoginTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Initializes the test environment before each test.
     *
     * - Starts the Spring Boot application context.
     * - Creates an HttpClient for sending HTTP requests.
     * - Initializes an ObjectMapper for JSON serialization and deserialization.
     * - Waits briefly to ensure the server is fully initialized.
     *
     * @throws InterruptedException if the thread is interrupted during startup delay.
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
     * - Shuts down the Spring Boot application context.
     *
     * @throws InterruptedException if the thread is interrupted during shutdown delay.
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Tests successful login with valid credentials.
     *
     * Sends an HTTP POST request to http://localhost:8080/login
     * with valid username and password. Expects:
     * 
     *   - Status Code: 200 OK
     *   - Response Body: JSON representation of the authenticated user
     *
     * @throws IOException if an I/O error occurs during the request.
     * @throws InterruptedException if the thread is interrupted while sending the request.
     */
    @Test
    public void loginSuccessful() throws IOException, InterruptedException {
    	String json = "{\"accountId\":0,\"username\":\"testuser1\",\"password\":\"password\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status);
        ObjectMapper om = new ObjectMapper();
        Account expectedResult = new Account(9999, "testuser1", "password");
        Account actualResult = om.readValue(response.body().toString(), Account.class);
        Assertions.assertEquals(expectedResult, actualResult);        
    }

    /**
     * Tests login with an invalid username.
     *
     * Sends an HTTP POST request to http://localhost:8080/login
     * with a username that does not exist. Expects:
     *
     *   - Status Code: 401 Unauthorized
     *
     * @throws IOException if an I/O error occurs during the request.
     * @throws InterruptedException if the thread is interrupted while sending the request.
     */
    @Test
    public void loginInvalidUsername() throws IOException, InterruptedException {
    	String json = "{\"accountId\":9999,\"username\":\"testuser404\",\"password\":\"password\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(401, status, "Expected Status Code 401 - Actual Code was: " + status);
    }
    

    /**
     * Tests login with an invalid password.
     *
     * Sends an HTTP POST request to http://localhost:8080/login
     * using a correct username but incorrect password. Expects:
     *
     *   - Status Code: 401 Unauthorized
     *
     * @throws IOException if an I/O error occurs during the request.
     * @throws InterruptedException if the thread is interrupted while sending the request.
     */
    @Test
    public void loginInvalidPassword() throws IOException, InterruptedException {
    	String json = "{\"accountId\":9999,\"username\":\"testuser1\",\"password\":\"pass404\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(401, status, "Expected Status Code 401 - Actual Code was: " + status);
    }
}