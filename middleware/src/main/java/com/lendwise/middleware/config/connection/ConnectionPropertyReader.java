package com.lendwise.middleware.config.connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/*
    @created 1/1/2026 8:19 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class ConnectionPropertyReader {
    private final ObjectMapper objectMapper;
    private final Gson gson;
    private Map<String, Object> connectionProperties;


    public String getValue(String secretKeyName) {
        if(!connectionProperties.containsKey(secretKeyName)){
            throw new RuntimeException("Secret key is not configured.");
        }
        return gson.toJson(connectionProperties.get(secretKeyName));
    }

    @PostConstruct
    public void readConnectionProperties() {
        // Load JSON file from resources folder
        try (InputStream inputStream = ConnectionPropertyReader.class
                .getClassLoader()
                .getResourceAsStream("ConnectionProperty.json")) {

            if (inputStream == null) {
                throw new IOException("Resource not found: ConnectionProperty.json");
            }

//            byte[] inputConnectionStr = new byte[inputStream.available()];
//            inputStream.read(inputConnectionStr);
//            String inputConnectionStrString = new String(inputConnectionStr);
//
//
//            log.info("Data in file: " +inputConnectionStrString);

            // Parse JSON to Map
            connectionProperties = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});

            log.info("ConnectionProperty.json file loaded successfully");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read ConnectionProperty.json", e);
        }
    }

}
