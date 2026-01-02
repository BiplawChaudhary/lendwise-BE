package com.lendwise.loanservice.config.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
    @created 1/1/2026 8:28 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableCaching
@ConditionalOnProperty(name = "lendwise.redis", havingValue = "true")
public class RedisConfig {
    private final ConnectionPropertyReader secretsService;

    @Value("${spring.profiles.active}")
    private String activeProfile;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(Objects.requireNonNull(configuration()));
        log.info("*********************Redis Connection Established*********************");
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() throws JsonProcessingException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

    private RedisStandaloneConfiguration configuration(){
        log.info("*********************Redis Connection Params Fetching*********************");
        String redisSecret = secretsService.getValue("rediscache");
        log.info("*********************Redis Connection Params Fetching Complete*********************");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> redisParams = new HashMap<>();
        try {
            redisParams = (Map<String,Object>) mapper.readValue(redisSecret, Map.class).get(activeProfile);
            log.info("*********************Redis Connection Params Mapped*********************");
        }catch (JsonProcessingException e){
            log.error("*********************Redis Connection Params Mapping error*********************");
            return null;
        }
        int port  = Integer.parseInt(redisParams.get("port").toString());
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisParams.get("host").toString());
        config.setPort(port);
        return config;
    }

}
