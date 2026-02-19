package com.lendwise.merchantservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.merchantservice.constants.ProcConstants;
import com.lendwise.merchantservice.utils.common.ConversionUtil;
import com.lendwise.merchantservice.utils.common.ProcedureCallUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
    @created 2/18/2026 8:40 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantEkycService {
    private final ProcedureCallUtil procedureCallUtil;
    private final ObjectMapper objectMapper;
    private final IamService iamService;
    private final NotificationQueueService notificationQueueService;

    public Object saveMerchantPersonalDetails(String urn, Map<String, Object> userPersonalDetails) throws JsonProcessingException {
        log.info("SAVING MERCHANT PERSONAL DATA: {}", userPersonalDetails);

        String userId = userPersonalDetails.get("userId").toString();

        Map<String, Object> procCallResposne = procedureCallUtil.callProc(
                urn,
                ProcConstants.SAVE_MERCHANT_PERSONAL_DETAILS,
                Long.parseLong(userId),
                objectMapper.writeValueAsString(userPersonalDetails)
        );
        log.info("PROC RESPONSE FOR SAVING PERSONAL DATA: {}", objectMapper.writeValueAsString(procCallResposne));

        procedureCallUtil.verifyProcedureResponse(urn, procCallResposne);
        iamService.refreshMerchantDetails(urn, userId);
        return new HashMap<>();
    }


    public Object saveMerchantAddressDetails(String urn, Map<String, Object> userAddressDetails) throws JsonProcessingException {
        log.info("SAVING MERCHANT ADDRESS DATA: {}", userAddressDetails);

        String userId = userAddressDetails.get("userId").toString();

        Map<String, Object> procCallResposne = procedureCallUtil.callProc(
                urn,
                ProcConstants.SAVE_MERCHANT_ADDRESS_DETAILS,
                Long.parseLong(userId),
                objectMapper.writeValueAsString(userAddressDetails)
        );
        log.info("PROC RESPONSE FOR SAVING ADDRESS DATA: {}", objectMapper.writeValueAsString(procCallResposne));

        procedureCallUtil.verifyProcedureResponse(urn, procCallResposne);
        iamService.refreshMerchantDetails(urn, userId);
        return new HashMap<>();
    }


    public Object saveMerchantBusinessDetails(String urn, Map<String, Object> userBusinessDetails) throws JsonProcessingException {
        log.info("SAVING MERCHANT BUSINESS DATA: {}", userBusinessDetails);

        String userId = userBusinessDetails.get("userId").toString();

        Map<String, Object> procCallResposne = procedureCallUtil.callProc(
                urn,
                ProcConstants.SAVE_MERCHANT_BUSINESS_DETAILS,
                Long.parseLong(userId),
                objectMapper.writeValueAsString(userBusinessDetails)
        );
        log.info("PROC RESPONSE FOR SAVING BUSINESS DATA: {}", objectMapper.writeValueAsString(procCallResposne));

        procedureCallUtil.verifyProcedureResponse(urn, procCallResposne);
        iamService.refreshMerchantDetails(urn, userId);
        return new HashMap<>();
    }


    public Object adminUpdateMerchantEkycStatus(String urn, Map<String, Object> requestDto) throws JsonProcessingException {
        log.info("UPDATING MERCHANT EKYC STATUS: {}", requestDto);

        Map<String, Object> procCallResposne = procedureCallUtil.callProc(
                urn,
                ProcConstants.ADMIN_UPDATE_MERCHANT_EKYC_STATUS,
                objectMapper.writeValueAsString(requestDto)
        );
        log.info("PROC RESPONSE FOR UPDATING MERCHANT EKYC STATUS: {}", objectMapper.writeValueAsString(procCallResposne));

        procedureCallUtil.verifyProcedureResponse(urn, procCallResposne);
        iamService.refreshMerchantDetails(urn, requestDto.get("userId").toString());

        notificationQueueService.publishNotificationDataToQueue(urn,
                ConversionUtil.convertData(procCallResposne.get("dbresponse_obj"), new TypeReference<Map<String, Object>>() {}));
        return new HashMap<>();
    }


    public Object fetchAllMerchantList(String urn, Map<String, Object> filterDto) throws JsonProcessingException {
        log.info("FETCHING ALL MERCHANT LIST {}", filterDto);

        Map<String, Object> procCallResposne = procedureCallUtil.callProc(
                urn,
                ProcConstants.ADMIN_FETCH_ALL_EKYC_LIST,
                objectMapper.writeValueAsString(filterDto)
        );
//        log.info("PROC RESPONSE FOR SAVING BUSINESS DATA: {}", objectMapper.writeValueAsString(procCallResposne));

        procedureCallUtil.verifyProcedureResponse(urn, procCallResposne);
        return ConversionUtil.convertData(
                procCallResposne.get("dbresponse_obj"),
                new TypeReference<Object>() {
                }
        );
    }


    public Object getUserProfileData(String urn, String userId) throws JsonProcessingException {
        log.info("PROCESSING WRITE SESSION PROFILE DATA FOR USER: {} ", userId);

        Map<String, Object> fetchUserProfileDataProcResponse = procedureCallUtil.callProc(
                urn,
                ProcConstants.GET_USER_PROFILE_DATA,
                objectMapper.writeValueAsString(Map.of("userId", userId))
        );

        procedureCallUtil.verifyProcedureResponse(urn, fetchUserProfileDataProcResponse);

        return ConversionUtil.convertData(
                fetchUserProfileDataProcResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {
                }
        );
    }
}
