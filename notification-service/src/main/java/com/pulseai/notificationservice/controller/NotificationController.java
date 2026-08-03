package com.pulseai.notificationservice.controller;

import com.pulseai.notificationservice.entity.Notification;
import com.pulseai.notificationservice.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification APIs")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @Operation(summary = "Endpoint for Notification")
    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("employeeId");
        return ResponseEntity.ok(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId));
    }

    @Operation(summary = "Endpoint for Notification")
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("employeeId");
        return ResponseEntity.ok(notificationRepository.countByRecipientIdAndIsReadFalse(userId));
    }

    @Operation(summary = "Endpoint for Notification")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("employeeId");
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null && notification.getRecipientId().equals(userId)) {
            notification.setRead(true);
            notificationRepository.save(notification);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
