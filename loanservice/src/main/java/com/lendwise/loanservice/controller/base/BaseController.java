package com.lendwise.loanservice.controller.base;

import com.lendwise.loanservice.dto.global.GlobalApiResponseDto;
import com.lendwise.loanservice.utils.common.MessageSourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
    @created 5/9/2025 3:44 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Component
public class BaseController {

    @Autowired
    private MessageSourceUtils messageSourceUtils;

    public ResponseEntity<GlobalApiResponseDto> createSuccessResponse(Object responseData, String messageKey, Object... messageArgs) {
        String message = messageSourceUtils.getMessage(messageKey, messageArgs);
        return new ResponseEntity<>(
                createSuccessResponse(responseData, message),
                HttpStatus.OK
        );
    }


    private GlobalApiResponseDto createSuccessResponse(Object data, String message) {
        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(200);
        response.setApiResponseMessage(message);
        response.setApiResponseTimestamp(LocalDateTime.now());

        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setData(data);

        response.setApiResponseData(apiResponseData);
        return response;
    }


    // Success response with pagination
    public ResponseEntity<GlobalApiResponseDto> createSuccessResponse(
            Object responseData,
            String pageIndex,
            String pageSize,
            String totalPages,
            String totalRecords,
            String messageKey,
            Object... messageArgs
    ) {
        String message = messageSourceUtils.getMessage(messageKey, messageArgs);
        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(200);
        response.setApiResponseMessage(message);
        response.setApiResponseTimestamp(LocalDateTime.now());

        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setData(responseData);
        apiResponseData.setPageIndex(pageIndex);
        apiResponseData.setPageSize(pageSize);
        apiResponseData.setTotalPages(totalPages);
        apiResponseData.setTotalRecords(totalRecords);

        response.setApiResponseData(apiResponseData);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Error response
    public ResponseEntity<GlobalApiResponseDto> createErrorResponse(Object errors, int code, String messageKey, Object... messageArgs) {
        String message = messageSourceUtils.getMessage(messageKey, messageArgs);

        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(code);
        response.setApiResponseMessage(message);
        response.setApiResponseTimestamp(LocalDateTime.now());

        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setErrors(errors);

        response.setApiResponseData(apiResponseData);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
