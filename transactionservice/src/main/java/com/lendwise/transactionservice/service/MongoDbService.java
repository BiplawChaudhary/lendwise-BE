package com.lendwise.transactionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    @created 2/25/2026 10:36 AM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class MongoDbService {
    private final MongoTemplate mongoTemplate;

    public void saveMongoTransactionHistoryInMongoDb(Object transactions, String fonePayId){
        log.info("SAVING TRANSACTION HISTORY FETCHED FROM PROVIDER INTO MONGO: ");
        String collectionName = "trans_history_" + fonePayId;
        mongoTemplate.insert(transactions, collectionName);
        log.info("SAVED SUCCESSFULLY TRANSACTION HISTORY FETCHED FROM PROVIDER INTO MONGO: ");
    }
}
