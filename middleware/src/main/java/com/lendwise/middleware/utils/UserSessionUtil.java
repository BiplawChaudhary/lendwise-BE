package com.lendwise.middleware.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.middleware.constants.RedisConstants;
import com.lendwise.middleware.exceptions.GenericException;
import com.lendwise.middleware.utils.common.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
    @created 2/18/2026 8:55 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionUtil {


    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    public Map<String, Object> fetchUserSessionData(String urn) throws JsonProcessingException {
        String userId  = MDC.get("userId");

        String merchantDataInRedis = redisUtil.get(RedisConstants.USER_SESSION_DATA.replace("{USERID}", userId),
                urn, false);

        if(merchantDataInRedis == null){
            throw new GenericException(urn, "LOGIN SESSION EXPIRED.", 401);
        }
        return objectMapper.readValue(merchantDataInRedis, new TypeReference<Map<String, Object>>() {
        });
    }


    public Long fetchLoggedInUserId(String urn) throws JsonProcessingException {
        Map<String, Object> userProfileData =  fetchUserSessionData(urn);

        Map<String, Object> userAccountProfileMap = (Map<String, Object>) userProfileData.get("userAccountProfile");
        return Long.parseLong(userAccountProfileMap.get("user_id").toString());
    }


    public Map<String, Object> fetchUserPersonalDetails(String urn) throws JsonProcessingException {
        Map<String, Object> userProfileData =  fetchUserSessionData(urn);

        return (Map<String, Object>) userProfileData.get("userPersonalDetails");
    }

    public Map<String, Object> fetchUserAddressDetails(String urn) throws JsonProcessingException {
        Map<String, Object> userProfileData =  fetchUserSessionData(urn);

        return (Map<String, Object>) userProfileData.get("userAddressDetails");
    }


    public Map<String, Object> fetchUserBusinessDetails(String urn) throws JsonProcessingException {
        Map<String, Object> userProfileData =  fetchUserSessionData(urn);

        return (Map<String, Object>) userProfileData.get("userBusinessDetails");
    }

    public Map<String, Object> fetchUserEkycDetails(String urn) throws JsonProcessingException {
        Map<String, Object> userProfileData =  fetchUserSessionData(urn);

        return (Map<String, Object>) userProfileData.get("userEkcStatus");
    }
}
