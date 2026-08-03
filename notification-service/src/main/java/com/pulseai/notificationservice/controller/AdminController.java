package com.pulseai.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/admin/kafka")
@Tag(name = "Admin APIs")
public class AdminController {

    @Operation(summary = "Endpoint for Admin")
    @GetMapping("/errors")
    public ResponseEntity<List<Map<String, Object>>> getKafkaErrors() {
        return ResponseEntity.ok(Arrays.asList(
                Map.of("topic", "survey-completed", "partition", 0, "offset", 14352, "error", "Failed to deserialize message", "timestamp", LocalDateTime.now().minusMinutes(12)),
                Map.of("topic", "employee-onboarded", "partition", 2, "offset", 983, "error", "Connection timeout", "timestamp", LocalDateTime.now().minusHours(2)),
                Map.of("topic", "survey-completed", "partition", 1, "offset", 14360, "error", "Invalid payload format", "timestamp", LocalDateTime.now().minusMinutes(2))
        ));
    }
}
