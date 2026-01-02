package com.lendwise.merchantservice.config.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/*
    @created 1/1/2026 8:30 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Configuration
@Slf4j
@EnableRabbit
@RequiredArgsConstructor
@ConditionalOnProperty(name = "lendwise.rabbitmq", havingValue = "true")
public class RabbitMQConfig {
    private final ConnectionPropertyReader connectionPropertyReader;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public ConnectionFactory connectionFactory() throws NoSuchAlgorithmException, KeyManagementException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        Map<String,Object> connParams = Objects.requireNonNull(getParams());
        connectionFactory.setHost(connParams.get("host").toString());
        connectionFactory.setPort(Integer.parseInt(connParams.get("port").toString()));
        connectionFactory.setUsername(connParams.get("username").toString());
        connectionFactory.setPassword(connParams.get("password").toString());
//        connectionFactory.getRabbitConnectionFactory().useSslProtocol();
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    private Map<String,Object> getParams() {
        log.info("***RabbitMQ Connection Params Fetching***");
//        String rabbitSecret = secretsService.getValue("rabbit-scm");
        String rabbitSecret = connectionPropertyReader.getValue("rabbitmq-scm");

        log.info("***RabbitMQ Connection Params Fetching Complete***");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object > rabbitParams = new HashMap<>();
        try {
            rabbitParams = mapper.readValue(rabbitSecret, Map.class);
            log.info("**RabbitMQ Connection Params Mapped***");
        }catch (JsonProcessingException e) {
            log.info("***RabbitMQ Connection Params Mapping Error***");
            return new HashMap<>();
        }
        return (Map<String, Object>) rabbitParams.get(activeProfile);
    }
}