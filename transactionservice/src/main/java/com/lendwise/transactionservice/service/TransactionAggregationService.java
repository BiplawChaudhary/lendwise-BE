package com.lendwise.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lendwise.transactionservice.constants.ProcConstants;
import com.lendwise.transactionservice.utils.common.ProcedureCallUtil;
import com.lendwise.transactionservice.utils.common.ServiceResponseUtil;
import com.lendwise.transactionservice.utils.common.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    @created 2/25/2026 10:09 AM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionAggregationService {

    private final MongoDbService mongoDbService;
    private final WebClientUtil webClientUtil;
    private final Gson gson;
    private final ProcedureCallUtil procedureCallUtil;
    private final ObjectMapper objectMapper;

    @Value("${lendwise.ai.model.url}")
    private String aiModelUrl;

    public Object processMerchantCreditScore(String urn, String userId,String userEmail, String fonePayId){
        log.info("Fetching fonepay transaction details for fonepayId: {} ", fonePayId);

        List<MerchantDatasetGenerator.Transaction> mockTransactionList = MerchantDatasetGenerator.generateMerchantTransactions(
                2,      // months
                50, 89, // txnPerDay range
                0.88,   // crProb
                1.0,    // amountMultiplier
                42L     // seed
        );

        log.info("Fonepay Transaction History Fetched successfully for fonepayId {} ", fonePayId);

        mongoDbService.saveMongoTransactionHistoryInMongoDb(mockTransactionList, fonePayId);

        new Thread(()->{
            try {
                asyncProcessMerchantCreditScore(urn, userId, fonePayId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return new HashMap<>();
    }


    public void asyncProcessMerchantCreditScore(String urn, String userId, String fonePayId) throws JsonProcessingException {
        log.info("CALLING DJANGO AI TO CALCULATE FONEPAY:{}", fonePayId);

        Map<String, Object> creditScoreApiResponse = webClientUtil
                .initiateGetRequest(
                        aiModelUrl.replace("{fonePayId}", fonePayId),
                        Map.of("urn", urn),
                        0, 0,
                        urn
                );

        log.info("AI MODEL API RESPONSE__{}:  {}",fonePayId, gson.toJson(creditScoreApiResponse));

        Map<String, Object> responseData = (Map<String, Object>) ServiceResponseUtil.validateAndGetResponseData(
                urn,
                creditScoreApiResponse
        );

        responseData.put("userId", userId);


        Map<String, Object> saveCreditScoreToDbProcResponse =
                procedureCallUtil.callProc(
                        urn,
                        ProcConstants.SAVE_MERCHANT_CREDIT_SCORE,
                        objectMapper.writeValueAsString(responseData)
                );
        log.info("SAVE MERCHANT CREDIT SCORE PROC RESPONSE_{}: {}",fonePayId, saveCreditScoreToDbProcResponse);

        procedureCallUtil.verifyProcedureResponse(urn, saveCreditScoreToDbProcResponse);

    }

}
