package com.lendwise.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private  String emailFrom;

    public void sendForgotPasswordEmail(String toEmail,
                                        String userName,
                                        String resetLinkExpirationTimeInHours,
                                        String resetLink) throws MessagingException {

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("resetLink", resetLink);
        context.setVariable("expirationTimeInHours", resetLinkExpirationTimeInHours);
        context.setVariable("logoUrl", "https://via.placeholder.com/150x50?text=LendWise+Logo");

        String htmlContent = templateEngine.process("ForgotPassword", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Reset Your Password - LendWise");
        helper.setText(htmlContent, true);
        helper.setFrom(emailFrom);

        mailSender.send(message);
    }
}
