package com.lendwise.middleware.exceptions;

import com.lendwise.middleware.dto.global.GlobalApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
    @created 5/9/2025 3:59 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle GenericException
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GlobalApiResponseDto> handleGenericException(GenericException ex) {
        log.error("Exception occurred with response code {} and message {} ",ex.getCode(), ex.getMessage());
        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(ex.getCode());
        response.setApiResponseMessage(ex.getMessage());
        response.setApiResponseTimestamp(LocalDateTime.now());
        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setData(new HashMap<>());
        response.setApiResponseData(apiResponseData);

        return ResponseEntity.ok(response);
    }

    // Handle GenericExceptionWithCustomData
    @ExceptionHandler(GenericExceptionWithCustomData.class)
    public ResponseEntity<GlobalApiResponseDto> handleGenericExceptionWithData(GenericExceptionWithCustomData ex) {
        log.error("Exception occurred with response code {} and message {} and data {}",ex.getCode(), ex.getMessage(), ex.getData());
        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(ex.getCode());
        response.setApiResponseMessage(ex.getMessage());
        response.setApiResponseTimestamp(LocalDateTime.now());

        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setErrors(ex.getData());

        response.setApiResponseData(apiResponseData);

        return ResponseEntity.ok(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalApiResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Exception occurred with: {}", ex.getMessage());

        Map<String, String> errorMap = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(422);
        response.setApiResponseMessage("Validation failed.");
        response.setApiResponseTimestamp(LocalDateTime.now());

        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setErrors(errorMap);

        response.setApiResponseData(apiResponseData);

        return ResponseEntity.ok(response);
    }

    // Handle all other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponseDto> handleGeneralException(Exception ex) {
        log.error("Exception occurred with: {}", ex.getMessage());
        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(500);
        response.setApiResponseMessage("Internal server error.");
        response.setApiResponseTimestamp(LocalDateTime.now());
        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setData(new HashMap<>());
        response.setApiResponseData(apiResponseData);

        // Optional: Log exception here

        return ResponseEntity.ok(response);
    }
}
