package com.lendwise.iam.exceptions;

import com.lendwise.iam.dto.global.GlobalApiResponseDto;
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
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle GenericException
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GlobalApiResponseDto> handleGenericException(GenericException ex) {
        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(ex.getCode());
        response.setApiResponseMessage(ex.getMessage());
        response.setApiResponseTimestamp(LocalDateTime.now());
        response.setApiResponseData(null); // no error object

        return ResponseEntity.ok(response);
    }

    // Handle GenericExceptionWithCustomData
    @ExceptionHandler(GenericExceptionWithCustomData.class)
    public ResponseEntity<GlobalApiResponseDto> handleGenericExceptionWithData(GenericExceptionWithCustomData ex) {
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
        GlobalApiResponseDto response = new GlobalApiResponseDto();
        response.setApiResponseCode(500);
        response.setApiResponseMessage("Internal server error.");
        response.setApiResponseTimestamp(LocalDateTime.now());
        response.setApiResponseData(null);

        // Optional: Log exception here

        return ResponseEntity.ok(response);
    }
}
