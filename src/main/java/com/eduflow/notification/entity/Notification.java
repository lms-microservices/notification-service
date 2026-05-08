package com.eduflow.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notification entity — persisted in notification_db.
 *
 * Schema (auto-created via ddl-auto=update):
 *   id          BIGSERIAL PRIMARY KEY
 *   user_id     BIGINT NOT NULL          -- recipient
 *   message     TEXT NOT NULL
 *   is_read     BOOLEAN DEFAULT false
 *   created_at  TIMESTAMP NOT NULL
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /* ── Lifecycle ──────────────────────────────────────────────────────────── */

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /* ── Constructors ───────────────────────────────────────────────────────── */

    public Notification() {}

    public Notification(Long userId, String message) {
        this.userId  = userId;
        this.message = message;
        this.isRead  = false;
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
