package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Social Media Application.
 */
@SpringBootApplication
public class SocialMediaApp {
    /**
     * Main method to run the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     * @throws InterruptedException if the thread executing the application is interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SocialMediaApp.class, args);
    }
}