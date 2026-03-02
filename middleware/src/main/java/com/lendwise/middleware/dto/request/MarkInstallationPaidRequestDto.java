package com.lendwise.middleware.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
    @created 2/26/2026 6:01 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Getter
@Setter
public class MarkInstallationPaidRequestDto {
    private String withdrawalId;
    private List<Integer> installmentIds;
}
