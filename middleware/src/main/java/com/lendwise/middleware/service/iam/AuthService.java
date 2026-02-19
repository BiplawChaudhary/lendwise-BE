package com.lendwise.middleware.service.iam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.lendwise.middleware.constants.SERVICE_URL_CONSTANTS;
import com.lendwise.middleware.dto.global.GlobalApiResponseDto;
import com.lendwise.middleware.dto.request.iam.AuthorizationRequestDto;
import com.lendwise.middleware.dto.request.iam.LoginRequestDto;
import com.lendwise.middleware.dto.request.iam.UserRegistrationRequestDto;
import com.lendwise.middleware.utils.UserSessionUtil;
import com.lendwise.middleware.utils.common.ServiceResponseUtil;
import com.lendwise.middleware.utils.common.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
    @created 2/13/2026 9:25 AM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final Gson gson;
    private final WebClientUtil webClientUtil;
    private final UserSessionUtil userSessionUtil;

    public Object loginMerchant(String urn, LoginRequestDto loginRequestDto) {
        log.info("LOGGING IN MERCHANT: {}", gson.toJson(loginRequestDto));

        Map<String, Object> loginRequestApiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.IAM.LOGIN,
                Map.of("urn", urn),
                loginRequestDto,
                0, 0, urn
        );

        log.info("LOGIN RESPONSE: {}", gson.toJson(loginRequestApiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  loginRequestApiResponse);
    }


    public Object forgotPassword(String urn, LoginRequestDto loginRequestDto) {
        log.info("PROCESSING FORGOT PASSWORD FOR MERCHANT: {}", gson.toJson(loginRequestDto));

        Map<String, Object> forgotPasswordApiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.IAM.FORGOT_PASSWORD,
                Map.of("urn", urn),
                loginRequestDto,
                0, 0, urn
        );

        log.info("PROCESSING FORGOT PASSWORD FOR MERCHANT RESPONSE: {}", gson.toJson(forgotPasswordApiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  forgotPasswordApiResponse);
    }

    public Object registerUser(String urn, UserRegistrationRequestDto requestDto) {
        log.info("REGISTERING MERCHANT: {}", gson.toJson(requestDto));

        requestDto.setUserRole("MERCHANT");

        Map<String, Object> registerMerchantApiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.IAM.REGISTER_MERCHANT,
                Map.of("urn", urn),
                requestDto,
                0, 0, urn
        );

        log.info("REGISTER MERCHANT RESPONSE: {}", gson.toJson(registerMerchantApiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  registerMerchantApiResponse);
    }


    public Object registerAdmin(String urn, UserRegistrationRequestDto requestDto) {
        log.info("REGISTERING ADMIN: {}", gson.toJson(requestDto));

        requestDto.setUserRole("ADMIN");

        Map<String, Object> registerMerchantApiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.IAM.REGISTER_ADMIN,
                Map.of("urn", urn),
                requestDto,
                0, 0, urn
        );

        log.info("REGISTER ADMIN RESPONSE: {}", gson.toJson(registerMerchantApiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  registerMerchantApiResponse);
    }

    public Object authenticateAndCheckAuthorization(String urn, AuthorizationRequestDto requestDto) {

            log.info("CHECKING AND VALIDATING TOKEN {}", gson.toJson(requestDto));
            Map<String, Object> checkAndValidateRequestApiResponse = webClientUtil.initiatePostRequest(
                    SERVICE_URL_CONSTANTS.IAM.CHECK_AND_VALIDATE_TOKEN,
                    Map.of("urn", urn),
                    requestDto,
                    0, 0, urn
            );

            log.info("CHECK AND VALIDATE TOKEN RESPONSE: {}", gson.toJson(checkAndValidateRequestApiResponse));
            return ServiceResponseUtil.validateAndGetResponseData(urn,  checkAndValidateRequestApiResponse);
    }


    public Object toggleUserActiveStatus(String urn, String userId) throws JsonProcessingException {
        log.info("TOGGELING USER ACTIVE STATUS {}", userId);

        Map<String, Object> requestDto = new HashMap<>();
        requestDto.put("userId", userId);
        requestDto.put("updatedById", userSessionUtil.fetchLoggedInUserId(urn));

        Map<String, Object> checkAndValidateRequestApiResponse = webClientUtil.initiatePostRequest(
                SERVICE_URL_CONSTANTS.IAM.TOGGLE_ACTIVE_STATUS,
                Map.of("urn", urn, "userId", userId),
                requestDto,
                0, 0, urn
        );

        log.info("TOGGELING USER ACTIVE STATUS RESPONSE: {}", gson.toJson(checkAndValidateRequestApiResponse));
        return ServiceResponseUtil.validateAndGetResponseData(urn,  checkAndValidateRequestApiResponse);
    }


}
