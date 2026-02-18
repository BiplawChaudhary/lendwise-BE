package com.lendwise.merchantservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.merchantservice.constants.ProcConstants;
import com.lendwise.merchantservice.utils.common.ProcedureCallUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

        iamService.refreshMerchantDetails(urn, userId);
        return new HashMap<>();
    }


    public Object saveMerchantBusinessDetails(String urn, Map<String, Object> userBusinessDetails) throws JsonProcessingException {
        log.info("SAVING MERCHANT BUSINESS DATA: {}", userBusinessDetails);

        String userId = userBusinessDetails.get("userId").toString();

        Map<String, Object> procCallResposne = procedureCallUtil.callProc(
                urn,
                ProcConstants.SAVE_MERCHANT_ADDRESS_DETAILS,
                Long.parseLong(userId),
                objectMapper.writeValueAsString(userBusinessDetails)
        );
        log.info("PROC RESPONSE FOR SAVING BUSINESS DATA: {}", objectMapper.writeValueAsString(procCallResposne));

        iamService.refreshMerchantDetails(urn, userId);
        return new HashMap<>();
    }
}
