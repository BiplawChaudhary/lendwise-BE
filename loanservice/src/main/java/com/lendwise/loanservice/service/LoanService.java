package com.lendwise.loanservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lendwise.loanservice.constants.ProcConstants;
import com.lendwise.loanservice.dto.request.LoanWithdrawalRequestDto;
import com.lendwise.loanservice.dto.request.MarkInstallationPaidRequestDto;
import com.lendwise.loanservice.utils.common.ConversionUtil;
import com.lendwise.loanservice.utils.common.ProcedureCallUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
    @created 2/26/2026 5:33 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@RequiredArgsConstructor
@Service
public class LoanService {
    private final ProcedureCallUtil procedureCallUtil;
    private final ObjectMapper objectMapper;
    private final Gson gson;
    private final RedisDataService redisDataService;


    public Object fetchMerchantDashboardSummary(String urn, String userId) throws JsonProcessingException {
        log.info("FETCHING MERCHANT DASHBOARD SUMMARY: ");

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                    urn,
                        ProcConstants.FETCH_MERCHANT_DASHBOARD_SUMMARY,
                        objectMapper.writeValueAsString(Map.of("userId", userId))
                );

        log.info("FETCHING MERCHANT DASHBOARD SUMMARY SUCCESSFULLY: ");
        procedureCallUtil.verifyProcedureResponse(urn, procResponse);

        return ConversionUtil.convertData(
                procResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {}
        );
    }




    public Object fetchAdminDashboardSummary(String urn) throws JsonProcessingException {
        log.info("FETCHING ADMIN DASHBOARD SUMMARY: ");

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                        urn,
                        ProcConstants.FETCH_ADMIN_DASHBOARD_SUMMARY,
                        objectMapper.writeValueAsString(new HashMap<>())
                );

        log.info("FETCHING ADMIN DASHBOARD SUMMARY SUCCESSFULLY: ");
        procedureCallUtil.verifyProcedureResponse(urn, procResponse);

        return ConversionUtil.convertData(
                procResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {}
        );
    }




    public Object processLoanWithdrawal(String urn, LoanWithdrawalRequestDto requestDto) throws JsonProcessingException {
        log.info("PROCESSING LOAN WITHDRAWAL FOR: {} ", gson.toJson(requestDto));

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                        urn,
                        ProcConstants.PROCESS_LOAN_WITHDRAWAL,
                        objectMapper.writeValueAsString(requestDto)
                );

        log.info("PROCESS LOAN WITHDRAWAL PROC RESPOSNE: {} ", gson.toJson(procResponse));

        procedureCallUtil.verifyProcedureResponse(urn, procResponse);

        Map<String, Object> returnData = ConversionUtil.convertData(
                procResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {}
        );

        redisDataService.saveLoanWithdrawalDataToRedis(
                urn,
                returnData.get("withdrawalId").toString(),
                        returnData
        );

        return returnData;
    }



    public Object markInstallationPaid(String urn, MarkInstallationPaidRequestDto requestDto) throws JsonProcessingException {
        log.info("MARKING INSTALLATION PAID FOR: {} ", gson.toJson(requestDto));

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                        urn,
                        ProcConstants.MARK_INSTALLMENT_PAID,
                        objectMapper.writeValueAsString(requestDto)
                );

        log.info("MARKING INSTALLATION PAID PROC RESPOSNE: {} ", gson.toJson(procResponse));

        procedureCallUtil.verifyProcedureResponse(urn, procResponse);

        return ConversionUtil.convertData(
                procResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {}
        );
    }



    public Object fetchMerchantLoans(String urn, String userId) throws JsonProcessingException {
        log.info("FETCHING MERCHANT LOANS: ");

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                        urn,
                        ProcConstants.GET_MERCHANT_LOANS,
                        objectMapper.writeValueAsString(Map.of("userId", userId))
                );

        log.info("FETCHING MERCHANT LOANS: ");
        procedureCallUtil.verifyProcedureResponse(urn, procResponse);

        return ConversionUtil.convertData(
                procResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {}
        );
    }

    public Object fetchMerchantLoansOffers(String urn, String userId) throws JsonProcessingException {
        log.info("FETCHING MERCHANT LOANS OFFERS: ");

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                        urn,
                        ProcConstants.GET_MERCHANT_LOANS_OFFER,
                        objectMapper.writeValueAsString(Map.of("userId", userId))
                );

        log.info("FETCHING MERCHANT LOANS OFFERS: ");
        procedureCallUtil.verifyProcedureResponse(urn, procResponse);

        return ConversionUtil.convertData(
                procResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {}
        );
    }


    public Object fetchInstallmentBreakdown(String urn, String withdrawalId) throws JsonProcessingException {
        log.info("FETCHING LOAN INSTALLMENT BRAKDOWN: ");

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                        urn,
                        ProcConstants.GET_INSTALLMENT_BREAKDOWN,
                        objectMapper.writeValueAsString(Map.of("withdrawalId", withdrawalId))
                );

        log.info("FETCHING LOAN INSTALLMENT BRAKDOWN SUCCESSFULLY: ");
        procedureCallUtil.verifyProcedureResponse(urn, procResponse);

        return ConversionUtil.convertData(
                procResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {}
        );
    }
}
