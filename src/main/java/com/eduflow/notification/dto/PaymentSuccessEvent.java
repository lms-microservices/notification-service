package com.eduflow.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO that mirrors the PaymentSuccessEvent published by payment-service.
 *
 * Fields must match exactly what payment-service serialises into RabbitMQ:
 *   - studentId  : Long
 *   - courseId   : Long
 *   - amount     : Double
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) makes this forward-compatible
 * if payment-service adds new fields later.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSuccessEvent {

    private Long   studentId;
    private Long   courseId;
    private Double amount;

    /* ── Constructors ───────────────────────────────────────────────────────── */

    public PaymentSuccessEvent() {}

    public PaymentSuccessEvent(Long studentId, Long courseId, Double amount) {
        this.studentId = studentId;
        this.courseId  = courseId;
        this.amount    = amount;
    }

    /* ── Getters & Setters ──────────────────────────────────────────────────── */

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "PaymentSuccessEvent{studentId=" + studentId +
                ", courseId=" + courseId +
                ", amount=" + amount + "}";
    }
}
