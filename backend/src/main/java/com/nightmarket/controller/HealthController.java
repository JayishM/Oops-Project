package com.nightmarket.controller;

<<<<<<< HEAD
import org.springframework.data.mongodb.core.MongoTemplate;
=======
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

<<<<<<< HEAD
/** Health endpoint that also exposes whether the configured MongoDB is reachable. */
@RestController
public class HealthController {

    private final MongoTemplate mongoTemplate;

    public HealthController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        String databaseStatus;
        try {
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            databaseStatus = "CONNECTED";
        } catch (Exception ex) {
            databaseStatus = "DISCONNECTED";
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("service", "nightmarket-backend");
        result.put("database", databaseStatus);
=======
/** Lightweight endpoint for checking that the backend has started successfully. */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("service", "nightmarket-backend");
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec
        result.put("timestamp", Instant.now().toString());
        return result;
    }
}
