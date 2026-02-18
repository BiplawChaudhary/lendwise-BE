package com.lendwise.middleware.constants;

/*
    @created 1/1/2026 9:00 PM
    @project lendwise
    @author biplaw.chaudhary
*/
public class ApiConstants {
    public static final String API_VERSION = "/api/v1";

    public static class AUTH{
        public static final String AUTH_BASE = "/auth";
        public static final String LOGIN = "/login";
        public static final String SAVE_MERCHANT = "/saveMerchant";
        public static final String SAVE_ADMIN = "/saveAdmin";
        public static final String AUTHENTICATE_TOKEN = "/authenticateToken";
        public static final String FORGOT_PASSWORD = "/forgotPassword";
        public static final String RESET_ACCOUNT_OF_MERCHANT = "/resetAccountOfMerchant";
        public static final String DEACTIVATE_MERCHANT = "/deactivateMerchant";
    }

}
