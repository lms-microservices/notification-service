package com.eduflow.notification.service;

import com.eduflow.notification.dto.NotificationResponse;
import com.eduflow.notification.entity.Notification;
import com.eduflow.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    /* ── Called by RabbitMQ listener ────────────────────────────────────────── */

    /**
     * Persist a new notification for a user.
     * Called internally from the RabbitMQ listener after a PaymentSuccessEvent.
     */
    public Notification createNotification(Long userId, String message) {
        Notification notification = new Notification(userId, message);
        return repo.save(notification);
    }

    /* ── REST API operations ─────────────────────────────────────────────────── */

    /**
     * GET /api/notifications — all notifications for the requesting user.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * PATCH /api/notifications/{notificationId}/read — mark one notification read.
     * Throws if not found or if it belongs to a different user.
     */
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        Notification notification = repo.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found or does not belong to you"));

        notification.setIsRead(true);
        return toResponse(repo.save(notification));
    }

    /**
     * DELETE /api/notifications/{notificationId} — delete one notification.
     * Throws if not found or if it belongs to a different user.
     */
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = repo.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found or does not belong to you"));

        repo.delete(notification);
    }

    /* ── Mapper ─────────────────────────────────────────────────────────────── */

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getUserId(),
                n.getMessage(),
                n.getIsRead(),
                n.getCreatedAt()
        );
    }

    /* ── Inner exception (keeps things self-contained) ───────────────────────── */

    public static class NotificationNotFoundException extends RuntimeException {
        public NotificationNotFoundException(String message) {
            super(message);
        }
    }
}
