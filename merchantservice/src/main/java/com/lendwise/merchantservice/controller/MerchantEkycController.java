package com.lendwise.merchantservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lendwise.merchantservice.constants.ApiConstants;
import com.lendwise.merchantservice.controller.base.BaseController;
import com.lendwise.merchantservice.service.MerchantEkycService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
    @created 5/9/2025 3:45 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Slf4j
@RestController
@RequestMapping(ApiConstants.API_VERSION)
@RequiredArgsConstructor
public class MerchantEkycController extends BaseController {
    private final MerchantEkycService merchantEkycService;

    @PostMapping(ApiConstants.MERCHANT_BASE + ApiConstants.SAVE_PERSONAL_DETAILS)
    public ResponseEntity<?> savePersonalDetails(
            @RequestHeader String urn,
            @RequestBody Map<String, Object> requestBody
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                merchantEkycService.saveMerchantPersonalDetails(urn, requestBody),
                "save.success", "Merchant Personal Details"
        );
    }


    @PostMapping(ApiConstants.MERCHANT_BASE + ApiConstants.SAVE_ADDRESS_DETAILS)
    public ResponseEntity<?> saveAddressDetails(
            @RequestHeader String urn,
            @RequestBody Map<String, Object> requestBody
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                merchantEkycService.saveMerchantAddressDetails(urn, requestBody),
                "save.success", "Merchant Address Details"
        );
    }



    @PostMapping(ApiConstants.MERCHANT_BASE + ApiConstants.SAVE_BUSINESS_DETAILS)
    public ResponseEntity<?> saveBusinessDetails(
            @RequestHeader String urn,
            @RequestBody Map<String, Object> requestBody
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                merchantEkycService.saveMerchantBusinessDetails(urn, requestBody),
                "save.success", "Merchant Business Details"
        );
    }

    @PostMapping(ApiConstants.ADMIN_BASE + ApiConstants.FETCH_ALL_MERCHANT_LIST)
    public ResponseEntity<?> fetchAllMerchantList(
            @RequestHeader String urn,
            @RequestBody Map<String, Object> requestBody
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                merchantEkycService.fetchAllMerchantList(urn, requestBody),
                "fetch.success", "Merchant List"
        );
    }


    @PostMapping(ApiConstants.ADMIN_BASE + ApiConstants.UPDATE_EKYC_STATUS)
    public ResponseEntity<?> updateMerchantEkycStatus(
            @RequestHeader String urn,
            @RequestBody Map<String, Object> requestBody
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                merchantEkycService.adminUpdateMerchantEkycStatus(urn, requestBody),
                "update.success", "Merchant EKYC Status"
        );
    }
    @GetMapping(ApiConstants.ADMIN_BASE + ApiConstants.GET_MERCHANT_INFO)
    public ResponseEntity<?> getMerchantInfo(
            @RequestHeader String urn,
            @RequestHeader String userId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                merchantEkycService.getUserProfileData(urn, userId),
                "update.success", "Merchant Info"
        );
    }

}
