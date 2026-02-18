package com.lendwise.iam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.iam.constants.ProcConstants;
import com.lendwise.iam.constants.RedisConstants;
import com.lendwise.iam.utils.common.ConversionUtil;
import com.lendwise.iam.utils.common.ProcedureCallUtil;
import com.lendwise.iam.utils.common.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
    @created 2/17/2026 6:32 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDataService {
    private final ProcedureCallUtil procedureCallUtil;
    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;

    @Value("${jwt.expiration}")
    private long sessionExpiration;


    public void writeUserProfileDataToRedis(String urn, String userId) throws JsonProcessingException {
        log.info("PROCESSING WRITE SESSION PROFILE DATA FOR USER: {} ", userId);

        Map<String, Object> fetchUserProfileDataProcResponse = procedureCallUtil.callProc(
                urn,
                ProcConstants.GET_USER_PROFILE_DATA,
                objectMapper.writeValueAsString(Map.of("userId", userId))
        );

        procedureCallUtil.verifyProcedureResponse(urn, fetchUserProfileDataProcResponse);

        Map<String, Object> dbResponseObj = ConversionUtil.convertData(
                fetchUserProfileDataProcResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {
                }
        );

        String sessionRedisKey = RedisConstants.USER_SESSION_DATA.replace("{USERID}", userId);
        redisUtil.save(sessionRedisKey,
                dbResponseObj,sessionExpiration-1000, TimeUnit.MILLISECONDS, urn, true);

        log.info("SAVED SESSION PROFILE DATA FOR USER: {} ", userId);
    }
}
