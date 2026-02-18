package com.lendwise.iam.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
    @created 2/13/2026 8:23 AM
    @project lendwise
    @author biplaw.chaudhary
*/
@Service
@RequiredArgsConstructor
public class UserSessionUtil {
    private final ObjectMapper objectMapper;

    public Long extractUserLoginId(){
        return 0l;
    }
}
