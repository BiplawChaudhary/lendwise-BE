package com.lendwise.loanservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lendwise.loanservice.constants.RedisConstants;
import com.lendwise.loanservice.exceptions.GenericException;
import com.lendwise.loanservice.utils.common.ConversionUtil;
import com.lendwise.loanservice.utils.common.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
    @created 2/26/2026 5:49 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDataService {


    private final RedisUtil redisUtil;

    public void saveLoanWithdrawalDataToRedis(String urn, String withdrawalId, Object data){
        log.info("SAVING LOAN WITHDRAWAL DATA TO REDIS ");
        String redisKey = RedisConstants.LOAN_WITHDRAWAL_REDIS_KEY.replace("{WITHDRAWAL_ID}", withdrawalId);

        redisUtil.save(
                redisKey,
                data,
                30, TimeUnit.MINUTES,
                urn, true
        );
        log.info("LOAN WITHDRAWAL SAVED TO REDIS ");
    }


    public Map<String, Object> getLoanWithdrawalDataFromRedis(String urn, String withdrawalId){
        log.info("FETCHING LOAN WITHDRAWAL DATA IN REDIS ");
        String redisKey = RedisConstants.LOAN_WITHDRAWAL_REDIS_KEY.replace("{WITHDRAWAL_ID}", withdrawalId);

       String dataInRedis =  redisUtil.get(
                redisKey,
                urn, true
        );
        if(dataInRedis == null){
            throw new GenericException(urn, "Withdrawal data not found in redis", 401);
        }

        log.info("LOAN WITHDRAWAL FETCHED SUCCESSFULLY TO REDIS ");
        return ConversionUtil.convertData(dataInRedis,
                new TypeReference<Map<String, Object>>() {
                });
    }
}
