package com.lendwise.middleware.service.merchant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.lendwise.middleware.constants.SERVICE_URL_CONSTANTS;
import com.lendwise.middleware.exceptions.GenericException;
import com.lendwise.middleware.utils.UserSessionUtil;
import com.lendwise.middleware.utils.common.ServiceResponseUtil;
import com.lendwise.middleware.utils.common.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
    @created 2/18/2026 9:08 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantEkycService {
    private final Gson gson;
    private final WebClientUtil webClientUtil;
    private final UserSessionUtil userSessionUtil;


    public Object saveMerchantPersonalDetails(String urn, Map<String, Object> requestDto) throws JsonProcessingException {
        log.info("SAVING MERCHANT PERSONAL DETAILS {}", gson.toJson(requestDto));

       requestDto.put("userId",  userSessionUtil.fetchLoggedInUserId(urn));

        Map<String, Object> apiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.MERCHANT.SAVE_PERSONAL_DETAILS,
                Map.of("urn", urn),
                requestDto,
                0, 0, urn
        );

        log.info("SAVE MERCHANT PERSONAL DETAILS RESPONSE: {}", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  apiResponse);
    }


    public Object saveMerchantAddressDetails(String urn, Map<String, Object> requestDto) throws JsonProcessingException {
        log.info("SAVING MERCHANT ADDRESS DETAILS {}", gson.toJson(requestDto));

        requestDto.put("userId",  userSessionUtil.fetchLoggedInUserId(urn));

        Map<String, Object> apiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.MERCHANT.SAVE_ADDRESS_DETAILS,
                Map.of("urn", urn),
                requestDto,
                0, 0, urn
        );

        log.info("SAVE MERCHANT PERSONAL ADDERESS RESPONSE: {}", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  apiResponse);
    }


    public Object saveMerchantBusinessDetails(String urn, Map<String, Object> requestDto) throws JsonProcessingException {
        log.info("SAVING MERCHANT BUSINESS DETAILS {}", gson.toJson(requestDto));

        requestDto.put("userId",  userSessionUtil.fetchLoggedInUserId(urn));

        Map<String, Object> apiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.MERCHANT.SAVE_BUSINESS_DETAILS,
                Map.of("urn", urn),
                requestDto,
                0, 0, urn
        );

        log.info("SAVE MERCHANT PERSONAL BUSINESS RESPONSE: {}", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  apiResponse);
    }


    public Object fetchMerchantEkycDataByStep(String urn, String ekycStatus) throws JsonProcessingException {
        log.info("FETCHING EMRCHANT EKYC DATA FOR STATUS:{}",ekycStatus);

        return switch (ekycStatus){
            case "PERSONAL_DETAILS"-> userSessionUtil.fetchUserPersonalDetails(urn);
            case "ADDRESS_DETAILS"-> userSessionUtil.fetchUserAddressDetails(urn);
            case "BUSINESS_DETAILS"-> userSessionUtil.fetchUserBusinessDetails(urn);
            case "EKYC_STATUS"-> userSessionUtil.fetchUserEkycDetails(urn);
            default -> throw new GenericException(urn, "INVALID status", 401);
        };
    }
}
