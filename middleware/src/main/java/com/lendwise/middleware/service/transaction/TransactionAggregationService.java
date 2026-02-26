package com.lendwise.middleware.service.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.lendwise.middleware.constants.SERVICE_URL_CONSTANTS;
import com.lendwise.middleware.utils.UserSessionUtil;
import com.lendwise.middleware.utils.common.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
    @created 2/26/2026 5:16 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionAggregationService {

    private final WebClientUtil webClientUtil;
    private final UserSessionUtil userSessionUtil;
    private final Gson gson;

    public Object processCreditScoreCalculation(String urn) throws JsonProcessingException {


        Map<String, String> requestHeaderMap = new HashMap<>();
        requestHeaderMap.put("urn", urn);
        requestHeaderMap.put("userId", userSessionUtil.fetchLoggedInUserId(urn).toString());
        requestHeaderMap.put("userEmail", userSessionUtil.fetchLoggedInUserEmail(urn));
        requestHeaderMap.put("fonePayId", userSessionUtil.fetchLoggedInUserFonePayId(urn));

        log.info("Calculating credit score {} ", gson.toJson(requestHeaderMap));

        Map<String,Object> creditScoreCalcApiResponse=webClientUtil.initiateGetRequest(
                SERVICE_URL_CONSTANTS.TRANSACTION.CALCULATE_CREDIT_SCORE,
                requestHeaderMap,
                0, 0, urn
        );

        log.info("API RESPONSE FROM CACLUCALTE CREDIT SCORE API: {}", creditScoreCalcApiResponse);

        return creditScoreCalcApiResponse;
    }
}
