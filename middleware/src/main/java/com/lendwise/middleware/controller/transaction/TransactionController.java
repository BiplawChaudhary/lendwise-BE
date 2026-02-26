package com.lendwise.middleware.controller.transaction;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.lendwise.middleware.constants.ApiConstants;
import com.lendwise.middleware.controller.base.BaseController;
import com.lendwise.middleware.service.transaction.TransactionAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    @created 5/9/2025 3:45 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Slf4j
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_VERSION )
@RestController
public class TransactionController extends BaseController {
    private final TransactionAggregationService transactionAggregationService;

    @PostMapping(ApiConstants.MERCHANT_BASE +ApiConstants.TRANSACTION.CALCULATE_CREDIT_SCORE)
    public ResponseEntity<?> calculateCreditScore(
            @RequestHeader String urn
    ) throws JsonProcessingException {
        return createSuccessResponse(
                transactionAggregationService.processCreditScoreCalculation(
                        urn
                ),
                "success.success", "Credit score calculation request submitted"
        );
    }
}
