package com.lendwise.middleware.controller.loans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lendwise.middleware.constants.ApiConstants;
import com.lendwise.middleware.controller.base.BaseController;
import com.lendwise.middleware.dto.request.LoanWithdrawalRequestDto;
import com.lendwise.middleware.dto.request.MarkInstallationPaidRequestDto;
import com.lendwise.middleware.service.loan.LoanService;
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


    @PostMapping(ApiConstants.MERCHANT_BASE+ ApiConstants.LOAN.PROCESS_LOAN_WITHDRAWAL)
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


    @PostMapping(ApiConstants.MERCHANT_BASE+ApiConstants.LOAN.MARK_INSTALLATION_PAID)
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


    @GetMapping(ApiConstants.MERCHANT_BASE+ApiConstants.LOAN.FETCH_MERCHANT_DASHBOARD)
    public ResponseEntity<?> fetchMerchantDashboard(
            @RequestHeader String urn
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchMerchantDashboardSummary(
                        urn
                ),
                "success.message", "Merchant Dashboard summary"
        );
    }


    @GetMapping(ApiConstants.ADMIN_BASE+ApiConstants.LOAN.FETCH_ADMIN_DASHBOARD)
    public ResponseEntity<?> fetchAdminDashboard(
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


    @GetMapping(ApiConstants.LOAN.FETCH_MERCHANT_LOAN)
    public ResponseEntity<?> fetchMerchantLoan(
            @RequestHeader String urn,
            @RequestHeader(required = false) String userId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchMerchantLoans(
                        urn, userId
                ),
                "success.message", "Merchant loans details"
        );
    }


    @GetMapping(ApiConstants.LOAN.FETCH_MERCHANT_LOAN_OFFERING)
    public ResponseEntity<?> fetchMerchantLoanOffer(
            @RequestHeader String urn,
            @RequestHeader(required = false) String userId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                loanService.fetchMerchantLoanOffer(
                        urn, userId
                ),
                "success.message", "Merchant loans details"
        );
    }


    @GetMapping(ApiConstants.LOAN.FETCH_INSTALLMENT_BREAKDOWN)
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
