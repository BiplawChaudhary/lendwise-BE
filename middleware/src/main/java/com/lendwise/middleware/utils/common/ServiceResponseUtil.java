package com.lendwise.middleware.utils.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lendwise.middleware.dto.global.GlobalApiResponseDto;
import com.lendwise.middleware.exceptions.GenericException;

import java.util.HashMap;
import java.util.Map;

/*
    @created 2/13/2026 9:29 AM
    @project lendwise
    @author biplaw.chaudhary
*/
public class ServiceResponseUtil {

    public static Object validateAndGetResponseData(String urn, Map<String, Object> serviceResponse){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        GlobalApiResponseDto globalApiResponseDto = objectMapper.convertValue(serviceResponse, GlobalApiResponseDto.class);
        if(serviceResponse == null){
            throw new GenericException(urn, "API response is null.", 401);
        }

        if(!globalApiResponseDto.getApiResponseCode().toString().equals("200")){
            throw new GenericException(urn,globalApiResponseDto.getApiResponseMessage(), globalApiResponseDto.getApiResponseCode());
        }
        return globalApiResponseDto.getApiResponseData() != null?globalApiResponseDto.getApiResponseData().getData(): new HashMap<>();
    }
}
