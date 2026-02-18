package com.lendwise.middleware.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lendwise.middleware.constants.ApiConstants;
import com.lendwise.middleware.controller.base.BaseController;
import com.lendwise.middleware.dto.request.iam.AuthorizationRequestDto;
import com.lendwise.middleware.dto.request.iam.LoginRequestDto;
import com.lendwise.middleware.dto.request.iam.UserRegistrationRequestDto;
import com.lendwise.middleware.service.iam.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
    @created 5/9/2025 3:45 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Slf4j
@RestController
@RequestMapping(ApiConstants.API_VERSION + ApiConstants.AUTH.AUTH_BASE)
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping(ApiConstants.AUTH.LOGIN)
    public ResponseEntity<?> loginUser(
            @RequestHeader String urn,
            @RequestBody LoginRequestDto loginRequestDto
    )  {
        MDC.put("urn", urn);
        return createSuccessResponse(
                authService.loginMerchant(urn, loginRequestDto),
                "auth.success");
    }

    @PostMapping(ApiConstants.AUTH.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(
            @RequestHeader String urn,
            @RequestBody LoginRequestDto loginRequestDto
    )  {
        MDC.put("urn", urn);
        return createSuccessResponse(
                authService.forgotPassword(urn, loginRequestDto),
                "auth.success");
    }


    @PostMapping(ApiConstants.AUTH.SAVE_MERCHANT)
    public ResponseEntity<?> saveMerchant(
            @RequestHeader String urn,
            @RequestBody UserRegistrationRequestDto requestDto
    )  {
        MDC.put("urn", urn);
        return createSuccessResponse(
                authService.registerUser (urn, requestDto),
                "save.success", "Merchant");
    }


    @PostMapping(ApiConstants.AUTH.SAVE_ADMIN)
    public ResponseEntity<?> saveAdmin(
            @RequestHeader String urn,
            @RequestBody UserRegistrationRequestDto requestDto
    )  {
        MDC.put("urn", urn);
        return createSuccessResponse(
                authService.registerAdmin(urn, requestDto),
                "save.success", "Admin");
    }

}
