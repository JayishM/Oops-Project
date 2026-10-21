package com.nightmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NightMarket Spring Boot Application Entry Point.
 * Runs the NightMarket REST API on port 8080 with MongoDB persistence.
 */
@SpringBootApplication
public class NightMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(NightMarketApplication.class, args);
    }
}
