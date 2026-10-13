package com.nightmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NightMarket Spring Boot Application Entry Point.
 * Runs an embedded Tomcat server on port 8080 with no database.
 */
@SpringBootApplication
public class NightMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(NightMarketApplication.class, args);
    }
}
