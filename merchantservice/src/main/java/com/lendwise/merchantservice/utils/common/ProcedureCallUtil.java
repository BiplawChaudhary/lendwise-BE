package com.lendwise.merchantservice.utils.common;


import com.google.gson.Gson;
import com.lendwise.merchantservice.constants.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "jarfusion.postgres", havingValue = "true")
public class ProcedureCallUtil {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    Logger logger = LoggerFactory.getLogger(ProcedureCallUtil.class);
    public Map<String,Object> callProc(String urn,String procName, Object... objects){
        StringBuilder queryBuilder = new StringBuilder(AppConstants.SELECT_QUERY);
        queryBuilder.append(procName).append(AppConstants.START_SMALL_BRACKET);
        for (int i = 0; i < objects.length; i++) {
            if(objects[i] instanceof String) queryBuilder
                    .append(AppConstants.SINGLE_QUOTE)
                    .append(objects[i])
                    .append(AppConstants.SINGLE_QUOTE);
            else queryBuilder.append(objects[i]);
            if (i < objects.length - 1) queryBuilder.append(AppConstants.COMMA);
        }
        queryBuilder.append(AppConstants.CLOSE_SMALL_BRACKET);
        logger.info("[URN_{}] , Raw Query:{}",urn,queryBuilder);
        Map<String, Object> procResponse = namedParameterJdbcTemplate.queryForMap(queryBuilder.toString(), new MapSqlParameterSource());
        logger.info("[URN_{}] , Procedure Response: {}",urn,new Gson().toJson(procResponse));
        return procResponse;
    }



    public List<Map<String,Object>> callProcForList(String urn,String procName, Object... objects){
        StringBuilder queryBuilder = new StringBuilder(AppConstants.SELECT_QUERY);
        queryBuilder.append(procName).append(AppConstants.START_SMALL_BRACKET);
        for (int i = 0; i < objects.length; i++) {
            if(objects[i] instanceof String) queryBuilder
                    .append(AppConstants.SINGLE_QUOTE)
                    .append(objects[i])
                    .append(AppConstants.SINGLE_QUOTE);
            else queryBuilder.append(objects[i]);
            if (i < objects.length - 1) queryBuilder.append(AppConstants.COMMA);
        }
        queryBuilder.append(AppConstants.CLOSE_SMALL_BRACKET);
        logger.info("[URN_{}] , query fired :{}",urn,queryBuilder.toString());
        return namedParameterJdbcTemplate.queryForList(queryBuilder.toString(), new HashMap<>());
    }

}