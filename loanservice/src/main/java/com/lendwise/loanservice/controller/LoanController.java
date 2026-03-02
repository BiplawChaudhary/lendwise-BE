package com.lendwise.loanservice.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.lendwise.loanservice.constants.ApiConstants;
import com.lendwise.loanservice.controller.base.BaseController;
import com.lendwise.loanservice.dto.request.LoanWithdrawalRequestDto;
import com.lendwise.loanservice.dto.request.MarkInstallationPaidRequestDto;
import com.lendwise.loanservice.service.LoanService;
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
public class LoanController extends BaseController {
    private final LoanService loanService;


    @PostMapping(ApiConstants.PROCESS_LOAN_WITHDRAWAL)
    public ResponseEntity<?> processLoanWithdrawal(
            @RequestHeader String urn,
            @RequestBody LoanWithdrawalRequestDto requestDto
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.processLoanWithdrawal(
                        urn, requestDto
                ),
                "success.message", "Loan withdrawal processed"
        );
    }


    @PostMapping(ApiConstants.MARK_INSTALLATION_PAID)
    public ResponseEntity<?> markInstallationPaid(
            @RequestHeader String urn,
            @RequestBody MarkInstallationPaidRequestDto requestDto
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.markInstallationPaid(
                        urn, requestDto
                ),
                "success.message", "Installment paid processed"
        );
    }


    @GetMapping(ApiConstants.FETCH_MERCHANT_DASHBOARD)
    public ResponseEntity<?> fetchMerchantDashboard(
            @RequestHeader String urn,
            @RequestHeader String userId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchMerchantDashboardSummary(
                        urn, userId
                ),
                "success.message", "Merchant Dashboard summary"
        );
    }


    @GetMapping(ApiConstants.FETCH_ADMIN_DASHBOARD)
    public ResponseEntity<?> fetchMerchantDashboard(
            @RequestHeader String urn
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchAdminDashboardSummary(
                        urn
                ),
                "success.message", "Admin Dashboard summary"
        );
    }


    @GetMapping(ApiConstants.FETCH_MERCHANT_LOAN)
    public ResponseEntity<?> fetchMerchantLoan(
            @RequestHeader String urn,
            @RequestHeader String userId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchMerchantLoans(
                        urn, userId
                ),
                "success.message", "Merchant loans details"
        );
    }

    @GetMapping(ApiConstants.FETCH_MERCHANT_LOAN_OFFERING)
    public ResponseEntity<?> fetchMerchantLoanOffering(
            @RequestHeader String urn,
            @RequestHeader String userId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchMerchantLoansOffers(
                        urn, userId
                ),
                "success.message", "Merchant loans offering details"
        );
    }


    @GetMapping(ApiConstants.FETCH_INSTALLMENT_BREAKDOWN)
    public ResponseEntity<?> fetchInstallmentBreakdown(
            @RequestHeader String urn,
            @RequestHeader String withdrawalId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchInstallmentBreakdown(
                        urn, withdrawalId
                ),
                "success.message", "Installment breakdown details"
        );
    }
}
