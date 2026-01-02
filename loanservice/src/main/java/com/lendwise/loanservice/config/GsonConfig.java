package com.lendwise.loanservice.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lendwise.loanservice.utils.common.LocalDateTimeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author Ozads
 * @version v1.0
 * @project jarFusion
 * @since 2024-07-22
 **/

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .disableHtmlEscaping()
                .create();
    }
}
