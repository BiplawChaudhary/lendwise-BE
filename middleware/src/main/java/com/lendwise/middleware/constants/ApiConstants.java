package com.lendwise.middleware.constants;

/*
    @created 1/1/2026 9:00 PM
    @project lendwise
    @author biplaw.chaudhary
*/
public class ApiConstants {
    public static final String API_VERSION = "/api/v1";

    public static final String MERCHANT_BASE = "/merchant";
    public static final String ADMIN_BASE = "/admin";


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


    public static class MERCHANT {

        public static final String SAVE_PERSONAL_DETAILS = "/savePersonalDetails";
        public static final String SAVE_ADDRESS_DETAILS = "/saveAddressDetails";
        public static final String SAVE_BUSINESS_DETAILS = "/saveBusinessDetails";
        public static final String FETCH_USER_KYC_DATA = "/fetchUserKycData";
    }

}
