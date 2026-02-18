package com.lendwise.merchantservice.service;

import com.google.gson.Gson;
import com.lendwise.merchantservice.utils.common.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
    @created 2/18/2026 8:37 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class IamService {
    public static final String MERCHANT_SESSION_REFRESH_URL = "http://localhost:9001/iam/api/v1/refreshSessionData";
    private final WebClientUtil webClientUtil;
    private final Gson gson;

    public void refreshMerchantDetails(String urn, String userId){
        log.info("REFRESHING MERCHANT PROFILE DATA: {}", userId);

        Map<String, Object> refreshMerchantSessionDataApiResponse = webClientUtil.initiateGetRequest(
                MERCHANT_SESSION_REFRESH_URL,
                Map.of(),
                0, 0, urn
        );

        log.info("MERCHANT PROFILE DATA REFRESHED SUCCESSFULLY: {}",gson.toJson(refreshMerchantSessionDataApiResponse));
    }
}
