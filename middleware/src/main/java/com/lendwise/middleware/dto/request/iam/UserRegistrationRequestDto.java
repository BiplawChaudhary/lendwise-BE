package com.lendwise.middleware.dto.request.iam;

import lombok.Getter;
import lombok.Setter;

/*
    @created 2/12/2026 1:08 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Getter
@Setter
public class UserRegistrationRequestDto {
    private String userEmail;
    private String userPassword;
    private String userRole;
}
