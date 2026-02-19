package com.lendwise.iam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.iam.constants.ProcConstants;
import com.lendwise.iam.constants.RedisConstants;
import com.lendwise.iam.dto.request.AuthorizationRequestDto;
import com.lendwise.iam.dto.request.LoginRequestDto;
import com.lendwise.iam.dto.request.UserRegistrationRequestDto;
import com.lendwise.iam.dto.response.LoginResponseDto;
import com.lendwise.iam.exceptions.GenericException;
import com.lendwise.iam.utils.JwtUtil;
import com.lendwise.iam.utils.common.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
    @created 2/12/2026 1:09 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final ProcedureCallUtil procedureCallUtil;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final RedisDataService redisDataService;
    private final SymmetricEncryptionUtil symmetricEncryptionUtil;
    private final NotificationQueueService notificationQueueService;

    @Value("${jwt.expiration}")
    private long sessionExpiration;

    @Value("${fronted.reset.url.link}")
    private String frontedResetUrl;

    @Value("${password.reset.link.ttl.in.hrs}")
    private String passwordResetLinkTtlInHrs;

    public LoginResponseDto loginMerchant(String urn, LoginRequestDto loginRequestDto) throws JsonProcessingException {
        log.info("PROCESSING LOGIN FOR USER: {} ", loginRequestDto.getUserEmail());

        Map<String,Object> checkUserRegistrationProcResponse = procedureCallUtil.callProc(
                urn,
                ProcConstants.CHECK_USER_REGISTRATION_STATUS_FOR_LOGIN,
                objectMapper.writeValueAsString(loginRequestDto)
        );

        log.info("PROC RESPONSE FOR CHECK USER REGISTRATION: {}", objectMapper.writeValueAsString(checkUserRegistrationProcResponse));

        procedureCallUtil.verifyProcedureResponse(
                urn, checkUserRegistrationProcResponse
        );


        Map<String, Object> claimsFromDb = ConversionUtil.convertData(
                checkUserRegistrationProcResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {
                }
        );

        String hashedPasswordFromDb = claimsFromDb.get("password").toString();
        Long userId = Long.parseLong(claimsFromDb.get("userId").toString());
        String userProfilePictureUrl =  claimsFromDb.get("profilePictureUrl").toString();

        log.info("VALIDATING USER PASSWORD FOR USER: {}", loginRequestDto.getUserEmail());
        if(!SHA256Util.sha256Hex(loginRequestDto.getUserPassword()).equals(hashedPasswordFromDb)){
            throw new GenericException(urn,"Username or password invalid.", 401);
        }

        log.info("PASSWORD VALIDATED SUCCESSFULLY FOR USER: {}", loginRequestDto.getUserEmail());
        Map<String, Object> claims =new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", claimsFromDb.get("role"));

        redisDataService.writeUserProfileDataToRedis(urn, userId.toString());
        log.info("GENERATING TOKEN FOR USER: {}",  loginRequestDto.getUserEmail());

        return new LoginResponseDto(jwtUtil.generateToken(claimsFromDb, loginRequestDto.getUserEmail()),
                loginRequestDto.getUserEmail(), userProfilePictureUrl
                , claimsFromDb.get("role").toString()
                , claimsFromDb.get("kycStatus").toString()
        );
    }



    public Object processForgotPassword(String urn, LoginRequestDto loginRequestDto) throws Exception {
        log.info("PROCESSING FORGOT PASSWORD FOR USER: {} ", loginRequestDto.getUserEmail());

        Map<String,Object> checkUserRegistrationProcResponse = procedureCallUtil.callProc(
                urn,
                ProcConstants.CHECK_USER_REGISTRATION_STATUS_FOR_LOGIN,
                objectMapper.writeValueAsString(loginRequestDto)
        );

        log.info("PROC RESPONSE FOR FORGOT PASSWORD: {}", objectMapper.writeValueAsString(checkUserRegistrationProcResponse));

        try{
            procedureCallUtil.verifyProcedureResponse(
                    urn, checkUserRegistrationProcResponse
            );
        }catch (Exception e){
            throw new GenericException(urn,"Reset link has been sent to your email if email exists.", 200);
        }


        Map<String, Object> claimsFromDb = ConversionUtil.convertData(
                checkUserRegistrationProcResponse.get("dbresponse_obj"),
                new TypeReference<Map<String, Object>>() {
                }
        );


        Long userId = Long.parseLong(claimsFromDb.get("userId").toString());

        String finalResetLink = frontedResetUrl + URLEncoder.encode(
                symmetricEncryptionUtil.encrypt(userId.toString(), SymmetricEncryptionUtil.EncryptionAlgorithm.AES_CBC),
                StandardCharsets.UTF_8
        );

        Map<String, Object> resetLinkEmailNotificationPayload = new HashMap<>();
        resetLinkEmailNotificationPayload.put("toEmail", loginRequestDto.getUserEmail());
        resetLinkEmailNotificationPayload.put("userName", "user");
        resetLinkEmailNotificationPayload.put("resetLinkExpirationTimeInHours", passwordResetLinkTtlInHrs);
        resetLinkEmailNotificationPayload.put("finalResetLink", finalResetLink);

        notificationQueueService.publishNotificationDataToQueue(urn,Map.of(
                "notificationType", "RESET_PASSWORD",
                "data", resetLinkEmailNotificationPayload) );

        return new HashMap<>();
    }



    public Object registerUser(String urn, UserRegistrationRequestDto requestDto) throws JsonProcessingException {
        log.info("REGISTERING USER: {}", requestDto.getUserEmail());

        requestDto.setUserPassword(SHA256Util.sha256Hex(requestDto.getUserPassword()));

        Map<String, Object> registerUserProcResponse = procedureCallUtil.callProc(
                urn,
                ProcConstants.REGISTER_USER ,
                objectMapper.writeValueAsString(requestDto)
        );

        procedureCallUtil.verifyProcedureResponse(urn, registerUserProcResponse);

//        Map<String, Object> dbResponseObj = ConversionUtil.convertData(
//                registerUserProcResponse.get("dbresponse_obj"),
//                new TypeReference<Map<String, Object>>() {
//                }
//        );

        log.info("USER REGISTRATION SUCCESSFULLY FOR USER: {}", requestDto.getUserEmail());
        return new HashMap<>();
    }


    public Object authenticateAndCheckAuthorization(String urn, AuthorizationRequestDto authorizationRequestDto)
            throws JsonProcessingException {

        log.info("VALIDATING AUTHENTICATION REQUEST FOR: {}",
                objectMapper.writeValueAsString(authorizationRequestDto));

        String accessedUrl = authorizationRequestDto.getAccessedUrl();

        // Check if URL is public (no authentication required)
        if (isPublicUrl(accessedUrl)) {
            log.info("Public URL accessed: {}", accessedUrl);
            return Map.of("authorized", true, "reason", "Public URL");
        }

        // Validate authentication token
        String authToken = authorizationRequestDto.getAuthToken();
        if (authToken == null || authToken.trim().isEmpty()) {
            throw new GenericException(urn, "Authentication token required", 401);
        }

        try{
            jwtUtil.validateToken(authToken);
        }catch (Exception e){
            throw new GenericException(urn,e.getMessage(), 401);
        }

        // Extract user role from token
        Claims extractedClaims = jwtUtil.extractAllClaims(authToken);
        String userRole = extractedClaims.get("role", String.class);
        Long userId = extractedClaims.get("userId", Long.class);

        if (userRole == null || userRole.trim().isEmpty()) {
            throw new GenericException(urn, "Role not found in token", 403);
        }

        // Check role-based authorization
        if (!isAuthorizedForUrl(userRole, accessedUrl)) {
            throw new GenericException(urn,
                    String.format("User with role '%s' not authorized to access '%s'", userRole, accessedUrl),
                    403);
        }

        log.info("User with role '{}' authorized to access '{}'", userRole, accessedUrl);
        return Map.of(
                "authorized", true,
                "role", userRole,
                "url", accessedUrl,
                "userId", userId
        );
    }

    private boolean isPublicUrl(String url) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        List<String> publicUrlPatterns = List.of(
                "/lendwisemw/api/v1/auth/login",
                "/lendwisemw/api/v1/auth/forgotPassword",
                "/lendwisemw/api/v1/auth/saveMerchant",
                "/iam/api/v1/authenticateToken",
                "/lendwisemw/api/v1/files/**"
        );

        return publicUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, url));
    }

    private boolean isAuthorizedForUrl(String role, String url) {
        AntPathMatcher pathMatcher = new AntPathMatcher();

        Map<String, List<String>> roleUrlMapping = Map.of(
                "ADMIN", List.of("/lendwisemw/api/v1/admin/**", "/lendwisemw/api/v1/auth/toggleActiveStatus"), // Admin can access both
                "MERCHANT", List.of("/lendwisemw/api/v1/merchant/**")
        );

        List<String> allowedPatterns = roleUrlMapping.get(role.toUpperCase());

        if (allowedPatterns == null) {
            log.warn("Unknown role: {}", role);
            return false;
        }

        return allowedPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, url));
    }


    public Object toggleUserActiveStatus(String urn, Map<String, Object> requestDto) throws JsonProcessingException {
        log.info("TOGGELING USER ACTIVE STATUS {}", requestDto);

        Map<String, Object> procResponse = procedureCallUtil
                .callProc(
                        urn,
                        ProcConstants.TOGGLE_USER_ACTIVE_STATUS,
                        objectMapper.writeValueAsString(requestDto)
                );

        log.info("TOGGLE USER ACTIVE STATUS PROC RESPONSE: {}", procResponse);

        procedureCallUtil.verifyProcedureResponse(urn, procResponse);
        return new HashMap<>();
    }
}
