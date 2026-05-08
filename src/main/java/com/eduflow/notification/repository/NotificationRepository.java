package com.eduflow.notification.repository;

import com.eduflow.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * All notifications for a user, newest first.
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find a specific notification and verify it belongs to the given user
     * — used to prevent one user from touching another user's notifications.
     */
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    /**
     * Count of unread notifications for a user (used for the bell badge).
     */
    long countByUserIdAndIsRead(Long userId, Boolean isRead);

    /**
     * Bulk mark-all-read for a user.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    int markAllReadByUserId(@Param("userId") Long userId);
}
