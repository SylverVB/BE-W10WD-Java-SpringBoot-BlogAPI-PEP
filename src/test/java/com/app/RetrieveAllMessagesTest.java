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
 * Integration test class for verifying the /messages endpoint functionality.
 * 
 * This class tests the retrieval of all messages through an HTTP GET request.
 * It uses Spring Boot to launch the application context and Java HTTP client to send requests.
 */
public class RetrieveAllMessagesTest {
	ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    /**
     * Sets up the test environment before each test case.
     * 
     * This includes starting the Spring Boot application, initializing
     * an HTTP client and a Jackson ObjectMapper for JSON processing.
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
     * Tears down the application context after each test case.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @AfterEach
    public void tearDown() throws InterruptedException {
    	Thread.sleep(500);
    	SpringApplication.exit(app);
    }
    
    /**
     * Sends an HTTP GET request to /messages to retrieve all available messages.
     *
     * Verifies that the server responds with HTTP 200 OK, and that the response body
     * matches the expected list of Message objects.
     *
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    @Test
    public void getAllMessagesMessagesAvailable() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/messages"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        List<Message> expectedResult = new ArrayList<Message>();
        expectedResult.add(new Message(9996, 9996, "test message 3", 1669947792L));
        expectedResult.add(new Message(9997, 9997, "test message 2", 1669947792L));
        expectedResult.add(new Message(9999, 9999, "test message 1", 1669947792L));
        List<Message> actualResult = objectMapper.readValue(response.body().toString(), new TypeReference<List<Message>>(){});
        Assertions.assertEquals(expectedResult, actualResult, "Expected="+expectedResult + ", Actual="+actualResult);
    }
}