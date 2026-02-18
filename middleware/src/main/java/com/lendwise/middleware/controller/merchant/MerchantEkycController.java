package com.lendwise.middleware.controller.merchant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lendwise.middleware.constants.ApiConstants;
import com.lendwise.middleware.controller.base.BaseController;
import com.lendwise.middleware.service.merchant.MerchantEkycService;
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

    @PostMapping(ApiConstants.MERCHANT_BASE + ApiConstants.MERCHANT.SAVE_PERSONAL_DETAILS)
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


    @PostMapping(ApiConstants.MERCHANT_BASE + ApiConstants.MERCHANT.SAVE_ADDRESS_DETAILS)
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



    @PostMapping(ApiConstants.MERCHANT_BASE + ApiConstants.MERCHANT.SAVE_BUSINESS_DETAILS)
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


    @GetMapping(ApiConstants.MERCHANT_BASE + ApiConstants.MERCHANT.FETCH_USER_KYC_DATA)
    public ResponseEntity<?> fetchUserKycData(
            @RequestHeader String urn,
            @RequestHeader String type
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                merchantEkycService.fetchMerchantEkycDataByStep(urn,type),
                "fetch.success", "Merchant "+type +" Details"
        );
    }


}
