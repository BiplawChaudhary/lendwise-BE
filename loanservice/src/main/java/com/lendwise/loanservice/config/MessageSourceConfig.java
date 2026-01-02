package com.lendwise.loanservice.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/*
    @created 5/9/2025 3:30 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages"); // Load from src/main/resources
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
