package com.lendwise.iam.dto.request;

import lombok.Getter;
import lombok.Setter;

/*
    @created 2/13/2026 8:57 AM
    @project lendwise
    @author biplaw.chaudhary
*/
@Getter
@Setter
public class AuthorizationRequestDto {
    private String authToken;
    private String accessedUrl;
    private String userRole;
}
