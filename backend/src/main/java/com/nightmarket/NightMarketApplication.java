package com.nightmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NightMarket Spring Boot Application Entry Point.
<<<<<<< HEAD
 * Runs the NightMarket REST API on port 8080 with MongoDB persistence.
=======
 * Runs an embedded Tomcat server on port 8080 with no database.
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec
 */
@SpringBootApplication
public class NightMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(NightMarketApplication.class, args);
    }
}
