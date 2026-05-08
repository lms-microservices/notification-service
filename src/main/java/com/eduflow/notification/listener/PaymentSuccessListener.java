package com.eduflow.notification.listener;

import com.eduflow.notification.config.RabbitMQConfig;
import com.eduflow.notification.dto.PaymentSuccessEvent;
import com.eduflow.notification.entity.Notification;
import com.eduflow.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ consumer for payment success events.
 *
 * Listens to: notification.payment.queue
 * Bound to  : payment.exchange  with routing key  payment.success
 *
 * Message flow:
 *   payment-service publishes PaymentSuccessEvent
 *     → RabbitMQ routes via payment.exchange / payment.success
 *       → notification.payment.queue
 *         → this listener deserialises the JSON and saves a Notification
 */
@Component
public class PaymentSuccessListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentSuccessListener.class);

    private final NotificationService notificationService;

    public PaymentSuccessListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Triggered automatically when a message arrives on notification.payment.queue.
     *
     * The Jackson2JsonMessageConverter (configured in RabbitMQConfig) deserialises
     * the raw JSON bytes into a PaymentSuccessEvent before this method is called.
     *
     * On success  → Spring auto-acks the message (acknowledge-mode: auto).
     * On exception → Spring nacks and retries up to 3 times (see application.yml).
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handlePaymentSuccess(PaymentSuccessEvent event) {

        log.info("Received PaymentSuccessEvent: {}", event);

        // 1. Extract relevant fields
        Long studentId = event.getStudentId();
        Long courseId  = event.getCourseId();

        if (studentId == null || courseId == null) {
            log.error("Invalid PaymentSuccessEvent — studentId or courseId is null. Discarding. Event: {}", event);
            // Returning without throwing prevents infinite retry on a permanently bad message.
            return;
        }

        // 2. Build notification message (per API Contract — Notification Trigger Summary)
        String message = "You have successfully enrolled in the course!";

        // 3. Persist the notification
        Notification saved = notificationService.createNotification(studentId, message);

        log.info("Notification saved — id={}, userId={}, courseId={}", saved.getId(), studentId, courseId);
    }
}
