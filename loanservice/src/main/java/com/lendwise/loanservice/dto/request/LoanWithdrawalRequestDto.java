package com.lendwise.loanservice.dto.request;

import lombok.Getter;
import lombok.Setter;

/*
    @created 2/26/2026 5:44 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Getter
@Setter
public class LoanWithdrawalRequestDto {
    private String userId;
    private String fonePayId;
    private Double requestedLoanAmount;
    private Integer requestedTenure;
}
