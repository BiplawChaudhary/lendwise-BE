package com.lendwise.middleware.service.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.lendwise.middleware.constants.SERVICE_URL_CONSTANTS;
import com.lendwise.middleware.dto.request.LoanWithdrawalRequestDto;
import com.lendwise.middleware.dto.request.MarkInstallationPaidRequestDto;
import com.lendwise.middleware.utils.UserSessionUtil;
import com.lendwise.middleware.utils.common.ServiceResponseUtil;
import com.lendwise.middleware.utils.common.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
    @created 2/26/2026 6:08 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@RequiredArgsConstructor
@Service
public class LoanService {
    private final WebClientUtil webClientUtil;
    private final UserSessionUtil userSessionUtil;
    private final Gson gson;


    public Object processLoanWithdrawal(String urn, LoanWithdrawalRequestDto requestDto) throws JsonProcessingException {
        log.info("Processing LoanWithdrawalRequestDto {}", gson.toJson(requestDto));

        requestDto.setUserId(userSessionUtil.fetchLoggedInUserId(urn).toString());
        requestDto.setFonePayId(userSessionUtil.fetchLoggedInUserFonePayId(urn).toString());

        Map<String, Object> apiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.LOAN.PROCESS_LOAN_WITHDRAWAL,
                Map.of("urn", urn),
                requestDto,
                0, 0, urn
        );
        log.info("PROCESS LOAN WITHDRAWAL API RESPOSE : ", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn, apiResponse);
    }

    public Object markInstallationPaid(String urn, MarkInstallationPaidRequestDto requestDto) {
        log.info("Processing mark installation paid {}", gson.toJson(requestDto));

        Map<String, Object> apiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.LOAN.MARK_INSTALLATION_PAID,
                Map.of("urn", urn),
                requestDto,
                0, 0, urn
        );
        log.info("MARK INSTALLATION PAID API RESPOSE : ", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn, apiResponse);
    }

    public Object fetchMerchantDashboardSummary(String urn) throws JsonProcessingException {
        log.info("Fetching merchant dashboard");

        Map<String, Object> apiResponse = webClientUtil.initiateGetRequest(
                SERVICE_URL_CONSTANTS.LOAN.FETCH_MERCHANT_DASHBOARD,
                Map.of("urn", urn, "userId", userSessionUtil.fetchLoggedInUserId(urn).toString()),
                0, 0, urn
        );
        log.info("FETCH MERCHANT DASHBOARD API RESPOSE : ", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn, apiResponse);
    }

    public Object fetchAdminDashboardSummary(String urn) {
        log.info("Fetching admin dashboard");

        Map<String, Object> apiResponse = webClientUtil.initiateGetRequest(
                SERVICE_URL_CONSTANTS.LOAN.FETCH_ADMIN_DASHBOARD,
                Map.of("urn", urn),
                0, 0, urn
        );
        log.info("FETCH ADMIN DASHBOARD API RESPOSE : ", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn, apiResponse);
    }

    public Object fetchMerchantLoans(String urn, String userId) throws JsonProcessingException {
        log.info("Fetching merchant loan");

        String userid = userId !=null? userId: userSessionUtil.fetchLoggedInUserId(urn).toString();

        Map<String, Object> apiResponse = webClientUtil.initiateGetRequest(
                SERVICE_URL_CONSTANTS.LOAN.FETCH_MERCHANT_LOAN,
                Map.of("urn", urn, "userId", userid),
                0, 0, urn
        );
        log.info("FETCH MERCHANT loan API RESPOSE : ", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn, apiResponse);
    }


    public Object fetchMerchantLoanOffer(String urn, String userId) throws JsonProcessingException {
        log.info("Fetching merchant loan offer");

        String userid = userId !=null? userId: userSessionUtil.fetchLoggedInUserId(urn).toString();

        Map<String, Object> apiResponse = webClientUtil.initiateGetRequest(
                SERVICE_URL_CONSTANTS.LOAN.FETCH_MERCHANT_LOAN_OFFERS,
                Map.of("urn", urn, "userId", userid),
                0, 0, urn
        );
        log.info("FETCH MERCHANT FETCH LOAN OFFER API RESPOSE : ", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn, apiResponse);
    }

    public Object fetchInstallmentBreakdown(String urn, String withdrawalId) {
        log.info("Fetching InstallMent Breakdown");

        Map<String, Object> apiResponse = webClientUtil.initiateGetRequest(
                SERVICE_URL_CONSTANTS.LOAN.FETCH_INSTALLMENT_BREAKDOWN,
                Map.of("urn", urn, "withdrawalId", withdrawalId),
                0, 0, urn
        );
        log.info("FETCH INSTALLMENT BREAKDOWN API RESPOSE : ", gson.toJson(apiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn, apiResponse);
    }
}
