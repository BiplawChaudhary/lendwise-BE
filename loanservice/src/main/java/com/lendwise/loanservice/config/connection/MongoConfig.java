package com.lendwise.loanservice.config.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.loanservice.utils.common.ApplicationPropertyHotLoader;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
    @created 1/1/2026 8:30 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "lendwise.mongo", havingValue = "true")
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final ConnectionPropertyReader secretsService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    protected String getDatabaseName() {
        return ApplicationPropertyHotLoader.getMessageKey("mongo.name");
    }

    @Override
    @Bean
    @Primary
    public MongoClient mongoClient() {
        String connectionString = null;
        try {
            connectionString = getMongoDBConnectionString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return MongoClients.create(connectionString);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    private String getMongoDBConnectionString() throws JsonProcessingException {
        log.info("****** Fetching MongoDB connection string from AWS Secrets Manager *********");
        String connectionString =  secretsService.getValue("mongodb-nosql");

        if (connectionString == null || connectionString.isEmpty()) {
            log.error("****** Failed to retrieve MongoDB connection string from AWS Secrets Manager ******");
            throw new IllegalStateException("******** MongoDB connection string not found in AWS Secrets Manager *****");
        }

        Map mongoConnectionMap = (Map<String,Object>) new ObjectMapper().readValue(connectionString, Map.class).get(activeProfile);
        log.info(" ***** Successfully retrieved MongoDB connection string ****");
        return String.valueOf(mongoConnectionMap.get("url")).replace("{databaseName}", getDatabaseName());
    }
}