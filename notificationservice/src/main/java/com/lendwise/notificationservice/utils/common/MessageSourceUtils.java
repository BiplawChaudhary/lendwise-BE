package com.lendwise.notificationservice.utils.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/*
    @created 5/9/2025 3:30 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Component
@RequiredArgsConstructor
public class MessageSourceUtils {
    private final MessageSource messageSource;

    Locale locale = LocaleContextHolder.getLocale();

    public String getMessage(String code) {
        return getMessage(code, null);
    }

    public String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, locale);
    }
}
