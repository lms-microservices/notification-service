package com.eduflow.notification.dto;

import java.time.LocalDateTime;

/**
 * Response DTO returned to the frontend.
 * Maps the Notification entity to a clean API shape.
 */
public class NotificationResponse {

    private Long          id;
    private Long          userId;
    private String        message;
    private Boolean       isRead;
    private LocalDateTime createdAt;

    /* ── Constructors ───────────────────────────────────────────────────────── */

    public NotificationResponse() {}

    public NotificationResponse(Long id, Long userId, String message,
                                Boolean isRead, LocalDateTime createdAt) {
        this.id        = id;
        this.userId    = userId;
        this.message   = message;
        this.isRead    = isRead;
        this.createdAt = createdAt;
    }

    /* ── Getters & Setters ──────────────────────────────────────────────────── */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}