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
        public static final String TOGGLE_ACTIVE_STATUS ="http://localhost:9001/iam/api/v1/toggleActiveStatus";
    }

    public static class MERCHANT{
        public static final String SAVE_PERSONAL_DETAILS = "http://localhost:9003/merchant/api/v1/merchant/savePersonalDetails";
        public static final String SAVE_ADDRESS_DETAILS = "http://localhost:9003/merchant/api/v1/merchant/saveAddressDetails";
        public static final String SAVE_BUSINESS_DETAILS = "http://localhost:9003/merchant/api/v1/merchant/saveBusinessDetails";

        public static final String FETCH_ALL_MERCHANT_LIST = "http://localhost:9003/merchant/api/v1/admin/merchantList";
        public static final String UPDATE_EKYC_STATUS = "http://localhost:9003/merchant/api/v1/admin/updateEkycStatus";
        public static final String GET_MERCHANT_INFO = "http://localhost:9003/merchant/api/v1/admin/getMerchantInfo";
    }

    public static class TRANSACTION{
        public static final String CALCULATE_CREDIT_SCORE = "http://localhost:9006/txn/api/v1/calculateCreditScore";
    }


    public static class LOAN{
        public static final String FETCH_MERCHANT_DASHBOARD = "http://localhost:9002/loan/api/v1/fetchMerchantDashboard";
        public static final String FETCH_ADMIN_DASHBOARD = "http://localhost:9002/loan/api/v1/fetchAdminDashboard";
        public static final String FETCH_INSTALLMENT_BREAKDOWN = "http://localhost:9002/loan/api/v1/fetchInstallmentBreakdown";
        public static final String FETCH_MERCHANT_LOAN = "http://localhost:9002/loan/api/v1/fetchMerchantLoan";
        public static final String FETCH_MERCHANT_LOAN_OFFERS = "http://localhost:9002/loan/api/v1/fetchMerchantLoanOffer";
        public static final String MARK_INSTALLATION_PAID = "http://localhost:9002/loan/api/v1/markInstallationPaid";
        public static final String PROCESS_LOAN_WITHDRAWAL = "http://localhost:9002/loan/api/v1/processLoanWithdrawal";
    }
}
