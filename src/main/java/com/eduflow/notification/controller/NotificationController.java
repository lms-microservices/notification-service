package com.eduflow.notification.controller;

import com.eduflow.notification.config.GlobalExceptionHandler.UnauthorizedException;
import com.eduflow.notification.dto.NotificationResponse;
import com.eduflow.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private Long extractUserId(String header) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException("X-User-Id header is missing or empty");
        }
        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("X-User-Id header is not a valid number: " + header);
        }
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestHeader("X-User-Id") String userIdHeader) {

        Long userId = extractUserId(userIdHeader);
        log.debug("GET /api/notifications — userId={}", userId);
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            @RequestHeader("X-User-Id") String userIdHeader) {

        Long userId = extractUserId(userIdHeader);
        log.debug("PATCH /api/notifications/{}/read — userId={}", notificationId, userId);
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, userId));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable Long notificationId,
            @RequestHeader("X-User-Id") String userIdHeader) {

        Long userId = extractUserId(userIdHeader);
        log.debug("DELETE /api/notifications/{} — userId={}", notificationId, userId);
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.ok(Map.of("message", "Notification deleted"));
    }
}