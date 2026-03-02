package com.lendwise.transactionservice.controller.auth;

import com.lendwise.transactionservice.constants.ApiConstants;
import com.lendwise.transactionservice.controller.base.BaseController;
import com.lendwise.transactionservice.service.TransactionAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
    @created 5/9/2025 3:45 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Slf4j
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_VERSION)
@RestController
public class TransactionController extends BaseController {
    private final TransactionAggregationService transactionAggregationService;

    @GetMapping(ApiConstants.CALCULATE_CREDIT_SCORE)
    public ResponseEntity<?> calculateCreditScore(
            @RequestHeader String urn,
            @RequestHeader String userId,
            @RequestHeader String userEmail,
            @RequestHeader String fonePayId
    ) {
        MDC.put("urn", urn);
        return createSuccessResponse(
                transactionAggregationService.processMerchantCreditScore(
                        urn, userId, userEmail, fonePayId
                ),
                "success.message", "Credit score calculation request submitted"
        );
    }
}
