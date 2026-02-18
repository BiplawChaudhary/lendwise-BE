package com.lendwise.middleware.constants;

/*
    @created 2/13/2026 9:25 AM
    @project lendwise
    @author biplaw.chaudhary
*/
public class SERVICE_URL_CONSTANTS {
    public static class IAM{
        public static final String LOGIN = "http://localhost:9001/iam/api/v1/login";
        public static final String REGISTER_MERCHANT = "http://localhost:9001/iam/api/v1/saveMerchant";
        public static final String REGISTER_ADMIN = "http://localhost:9001/iam/api/v1/saveAdmin";
        public static final String CHECK_AND_VALIDATE_TOKEN = "http://localhost:9001/iam/api/v1/authenticateToken";
        public static final String FORGOT_PASSWORD ="http://localhost:9001/iam/api/v1/forgotPassword";
    }
}
