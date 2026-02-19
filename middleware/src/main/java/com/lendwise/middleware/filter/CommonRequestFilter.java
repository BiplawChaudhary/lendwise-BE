package com.lendwise.middleware.filter;

import com.google.gson.Gson;
import com.lendwise.middleware.dto.global.GlobalApiResponseDto;
import com.lendwise.middleware.dto.request.iam.AuthorizationRequestDto;
import com.lendwise.middleware.exceptions.GenericException;
import com.lendwise.middleware.exceptions.GenericExceptionWithCustomData;
import com.lendwise.middleware.service.iam.AuthService;
import com.lendwise.middleware.utils.common.CachedBodyHttpServletRequest;
import com.lendwise.middleware.utils.common.TimestampSequenceGenerator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/*
    @created 2/13/2026 9:40 AM
    @project lendwise
    @author biplaw.chaudhary
*/
@Slf4j
@RequiredArgsConstructor
@Component
public class CommonRequestFilter implements Filter {
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private final Gson gson;
    private final AuthService authService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String contentType = request.getContentType();
            String urn = request.getHeader("urn") == null ?
                    TimestampSequenceGenerator.generateUniqueIdInString() :
                    request.getHeader("urn");
            MDC.put("urn", urn);

            String header = request.getHeader("authToken");

            AuthorizationRequestDto authorizationRequestDto = new AuthorizationRequestDto();
            authorizationRequestDto.setAuthToken(header);
            authorizationRequestDto.setAccessedUrl(request.getRequestURI());

            // This might throw GenericException
            Object  authResponse= authService.authenticateAndCheckAuthorization(urn, authorizationRequestDto);

            if(authResponse!= null){
                Map<String, Object> authResponseData = (Map<String, Object>) authResponse;
                if(authResponseData.get("userId")!=null){
                    MDC.put("userId", authResponseData.get("userId"));
                }
            }

            if (contentType != null) {
                if (contentType.contains(MULTIPART_FORM_DATA)) {
                    handleMultipartRequest(request, servletResponse, filterChain, urn);
                } else {
                    handleRegularRequest(request, servletResponse, filterChain, urn);
                }
            } else {
                filterChain.doFilter(request, servletResponse);
            }

        } catch (GenericException ex) {
            handleGenericException(response, ex);
        } catch (GenericExceptionWithCustomData ex) {
            handleGenericExceptionWithCustomData(response, ex);
        } catch (Exception ex) {
            log.error("Unexpected error in filter", ex);
            handleGeneralException(response, ex);
        }
    }


    private void handleRegularRequest(HttpServletRequest request, ServletResponse servletResponse,

                                      FilterChain filterChain, String urn) throws IOException, ServletException {


        if (hasRequestBody(request.getMethod())) {
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            String reqBody = new String(cachedRequest.getCachedBody());
            log.info("[URN_{}] Entering into {} with Request Received : {}\n", urn, request.getRequestURI(), reqBody);
            logHeaders(request, urn);
            filterChain.doFilter(cachedRequest, servletResponse);
        } else {
            log.info("[URN_{}] Entering into {}  \n", urn, request.getRequestURI());
            logHeaders(request, urn);
            filterChain.doFilter(request, servletResponse);
        }

    }


    private boolean hasRequestBody(String method) {
        return "POST".equalsIgnoreCase(method) ||
                "PUT".equalsIgnoreCase(method) ||
                "PATCH".equalsIgnoreCase(method) ||
                "DELETE".equalsIgnoreCase(method);
    }

    private void handleMultipartRequest(HttpServletRequest request, ServletResponse servletResponse,
                                        FilterChain filterChain, String urn) throws IOException, ServletException {
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

            if (!fileMap.isEmpty()) {
                log.info("[URN_{}] Form data with {} file(s) detected", urn, fileMap.size());
            }
        }
        logHeaders(request, urn);
        filterChain.doFilter(request, servletResponse);
    }


    private void logHeaders(HttpServletRequest request, String urn) {
        Map<String, Object> headerDetails = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerDetails.put(headerName, request.getHeader(headerName));
        }
        log.info("[URN_{}] Header details as JSONObject: {} ", urn, gson.toJson(headerDetails));
    }


    private void handleGenericException(HttpServletResponse response, GenericException ex) throws IOException {
        GlobalApiResponseDto apiResponse = new GlobalApiResponseDto();
        apiResponse.setApiResponseCode(ex.getCode());
        apiResponse.setApiResponseMessage(ex.getMessage());
        apiResponse.setApiResponseTimestamp(LocalDateTime.now());
        apiResponse.setApiResponseData(null);

        writeJsonResponse(response, apiResponse, 200);
    }

    private void handleGenericExceptionWithCustomData(HttpServletResponse response, GenericExceptionWithCustomData ex)
            throws IOException {
        GlobalApiResponseDto apiResponse = new GlobalApiResponseDto();
        apiResponse.setApiResponseCode(ex.getCode());
        apiResponse.setApiResponseMessage(ex.getMessage());
        apiResponse.setApiResponseTimestamp(LocalDateTime.now());

        GlobalApiResponseDto.ApiResponseData apiResponseData = new GlobalApiResponseDto.ApiResponseData();
        apiResponseData.setErrors(ex.getData());
        apiResponse.setApiResponseData(apiResponseData);

        writeJsonResponse(response, apiResponse, 200);
    }

    private void handleGeneralException(HttpServletResponse response, Exception ex) throws IOException {
        GlobalApiResponseDto apiResponse = new GlobalApiResponseDto();
        apiResponse.setApiResponseCode(500);
        apiResponse.setApiResponseMessage("Internal server error.");
        apiResponse.setApiResponseTimestamp(LocalDateTime.now());
        apiResponse.setApiResponseData(null);

        writeJsonResponse(response, apiResponse, 200);
    }

    private void writeJsonResponse(HttpServletResponse response, GlobalApiResponseDto apiResponse, int statusCode)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(apiResponse));
        response.getWriter().flush();
    }
}
