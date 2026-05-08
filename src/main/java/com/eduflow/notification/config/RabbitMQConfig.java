package com.eduflow.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for the Notification Service (Consumer side).
 *
 * Topology:
 *   Exchange : payment.exchange  (TopicExchange — already declared by payment-service)
 *   Queue    : notification.payment.queue
 *   Binding  : payment.success  → notification.payment.queue
 *
 * Both services declare the same exchange so whichever starts first wins;
 * subsequent declarations are idempotent as long as arguments match.
 */
@Configuration
public class RabbitMQConfig {

    /* ── Constants ─────────────────────────────────────────────────────────── */

    public static final String PAYMENT_EXCHANGE       = "payment.exchange";
    public static final String NOTIFICATION_QUEUE     = "notification.payment.queue";
    public static final String PAYMENT_SUCCESS_KEY    = "payment.success";

    /* ── Exchange ───────────────────────────────────────────────────────────── */

    /**
     * Declare the same exchange as payment-service so this service can bind to it.
     * durable=true → survives broker restart.
     */
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE, true, false);
    }

    /* ── Queue ──────────────────────────────────────────────────────────────── */

    /**
     * Durable queue — messages survive broker restart.
     */
    @Bean
    public Queue notificationPaymentQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    /* ── Binding ────────────────────────────────────────────────────────────── */

    @Bean
    public Binding notificationBinding(Queue notificationPaymentQueue,
                                       TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(notificationPaymentQueue)
                .to(paymentExchange)
                .with(PAYMENT_SUCCESS_KEY);
    }

    /* ── Message Converter ──────────────────────────────────────────────────── */

    /**
     * Deserialise incoming JSON messages to Java objects (PaymentSuccessEvent).
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Apply the JSON converter to the default RabbitTemplate (used for any
     * outbound messages the service might publish in the future).
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

    /**
     * Apply the JSON converter to the listener container factory so that
     * @RabbitListener methods receive deserialized objects, not raw bytes.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }
}
