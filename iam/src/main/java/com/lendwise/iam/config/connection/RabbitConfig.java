package com.lendwise.iam.config.connection;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
/*
    @created 1/1/2026 8:30 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Configuration
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "lendwise.rabbitmq", havingValue = "true")
public class RabbitConfig {

    public static final String LENDWISE_EXCHANGE = "lendwise-exchange";
    public static final String LENDWISE_NOTIFICATION_QUEUE = "lendwise-notification-queue";
    public static final String LENDWISE_NOTIFICATION_ROUTING_KEY = "lendwise-notification-routing-key";

    private final RabbitAdmin rabbitAdmin;


    @PostConstruct
    public void rabbitInit() {
        declareExchangeAndQueue(LENDWISE_EXCHANGE, LENDWISE_NOTIFICATION_QUEUE, LENDWISE_NOTIFICATION_ROUTING_KEY);
    }


    private void declareExchangeAndQueue(String exchangeName, String queueName, String routingKey) {
        try {
            rabbitAdmin.declareExchange(new TopicExchange(exchangeName, true, false));
            rabbitAdmin.declareQueue(new Queue(queueName, true));
            rabbitAdmin.declareBinding(
                    BindingBuilder.bind(new Queue(queueName, true))
                            .to(new TopicExchange(exchangeName, true, false))
                            .with(routingKey));
            log.info("Initialized RabbitMQ configuration for exchange '{}' and queue '{}'", exchangeName, queueName);
        } catch (AmqpException e) {
            log.error("Error initializing RabbitMQ configuration for exchange '{}' and queue '{}'", exchangeName, queueName, e);
        }
    }

}