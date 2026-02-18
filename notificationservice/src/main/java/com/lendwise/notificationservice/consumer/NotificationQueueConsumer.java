package com.lendwise.notificationservice.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.notificationservice.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
    @created 2/17/2026 6:18 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQueueConsumer {
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "lendwise-notification-queue")
    public void processNotification(Message message) {
        String rawMessage = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Received notification message from queue: {}", rawMessage);
        try {
            Map<String, Object> root =
                    objectMapper.readValue(rawMessage,
                            new TypeReference<Map<String, Object>>() {});

            String notificationType =
                    (String) root.get("notificationType");

            if (notificationType == null) {
                log.warn("Notification type is missing in message");
                return;
            }

            if ("RESET_PASSWORD".equalsIgnoreCase(notificationType)) {
                processPasswordResetLinkEmail(root);
            }else{
                log.warn("Unsupported notification type: {}", notificationType);
            }

        } catch (MessagingException e) {
            log.error("Failed to send reset password email", e);
            throw new RuntimeException("Email sending failed", e);

        } catch (Exception e) {
            log.error("Error processing notification message", e);
            throw new RuntimeException("Notification processing failed", e);
        }
    }

    private void processPasswordResetLinkEmail(Map<String, Object> root) throws MessagingException {
        Object dataObj = root.get("data");

        if (!(dataObj instanceof Map)) {
            log.error("Invalid data structure in notification message");
            return;
        }

        Map<String, Object> data =
                (Map<String, Object>) dataObj;

        String toEmail =
                (String) data.get("toEmail");
        String userName =
                (String) data.get("userName");
        String resetLinkExpirationTimeInHours =
                String.valueOf(data.get("resetLinkExpirationTimeInHours"));
        String resetLink =
                (String) data.get("finalResetLink");

        if (toEmail == null || resetLink == null) {
            log.error("Required fields missing for RESET_PASSWORD notification");
            return;
        }

        log.info("Processing RESET_PASSWORD notification for email: {}", toEmail);

        emailService.sendForgotPasswordEmail(
                toEmail,
                userName,
                resetLinkExpirationTimeInHours,
                resetLink
        );

        log.info("Reset password email sent successfully to {}", toEmail);
    }

}
