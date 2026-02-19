package com.lendwise.iam.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lendwise.iam.constants.ApiConstants;
import com.lendwise.iam.controller.base.BaseController;
import com.lendwise.iam.dto.request.AuthorizationRequestDto;
import com.lendwise.iam.dto.request.LoginRequestDto;
import com.lendwise.iam.dto.request.UserRegistrationRequestDto;
import com.lendwise.iam.service.AuthService;
import com.lendwise.iam.service.RedisDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
    @created 5/9/2025 3:45 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Slf4j
@RestController
@RequestMapping(ApiConstants.API_VERSION)
@RequiredArgsConstructor
public class AuthController extends BaseController {


    private final AuthService authService;
    private final RedisDataService redisDataService;

    @PostMapping(ApiConstants.LOGIN)
    public ResponseEntity<?> loginUser(
            @RequestHeader String urn,
            @RequestBody LoginRequestDto loginRequestDto
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        return createSuccessResponse(
                authService.loginMerchant(urn, loginRequestDto),
                "auth.success");
    }


    @PostMapping(ApiConstants.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(
            @RequestHeader String urn,
            @RequestBody LoginRequestDto loginRequestDto
    ) throws Exception {
        MDC.put("urn", urn);
        return createSuccessResponse(
                authService.processForgotPassword(urn, loginRequestDto),
                "send.success", "Reset link has been ");
    }


    @PostMapping(ApiConstants.SAVE_MERCHANT)
    public ResponseEntity<?> saveMerchant(
            @RequestHeader String urn,
            @RequestBody UserRegistrationRequestDto requestDto
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        requestDto.setUserRole("MERCHANT");
        return createSuccessResponse(
                authService.registerUser (urn, requestDto),
                "save.success", "Merchant");
    }


    @PostMapping(ApiConstants.SAVE_ADMIN)
    public ResponseEntity<?> saveAdmin(
            @RequestHeader String urn,
            @RequestBody UserRegistrationRequestDto requestDto
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        requestDto.setUserRole("ADMIN");
        return createSuccessResponse(
                authService.registerUser(urn, requestDto),
                "save.success", "Admin");
    }


    @PostMapping(ApiConstants.AUTHENTICATE_TOKEN)
    public ResponseEntity<?> authenticateToken(
            @RequestHeader String urn,
            @RequestBody AuthorizationRequestDto requestDto
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        requestDto.setUserRole("ADMIN");
        return createSuccessResponse(
                authService.authenticateAndCheckAuthorization(urn, requestDto),
                "verify.success", "Token");
    }


    @GetMapping(ApiConstants.REFRESH_SESSION_DATA)
    public ResponseEntity<?> refreshSessionData(
            @RequestHeader String urn,
            @RequestHeader String userId
    ) throws JsonProcessingException {
        MDC.put("urn", urn);
        redisDataService.writeUserProfileDataToRedis(urn, userId);
        return  createSuccessResponse(
                new HashMap<>() ,
                "update.success", "Session data"
        );
    }


    @PostMapping(ApiConstants.TOGGLE_ACTIVE_STATUS)
    public ResponseEntity<?> toggleActiveStatus(@RequestHeader String urn,
                                                @RequestBody Map<String, Object> requestDto) throws JsonProcessingException {
        return createSuccessResponse(
                authService.toggleUserActiveStatus(urn, requestDto),
                "update.success", "User active status"
        );
    }


}
