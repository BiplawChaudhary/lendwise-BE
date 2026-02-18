package com.lendwise.iam.service;

import com.google.gson.Gson;
import com.lendwise.iam.config.connection.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
    @created 2/17/2026 6:26 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationQueueService {
    private final RabbitTemplate rabbitTemplate;
    private final Gson gson;

    public void publishNotificationDataToQueue(String urn, Map<String, Object> data){
        log.info("Publishing notification data to queue: {}",gson.toJson(data) );
        rabbitTemplate.convertAndSend(
                RabbitConfig.LENDWISE_EXCHANGE,
                RabbitConfig.LENDWISE_NOTIFICATION_ROUTING_KEY,
                data
        );
        log.info("Notification Data Pubished Successfully.");
    }

}
