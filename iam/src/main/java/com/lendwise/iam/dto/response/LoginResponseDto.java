package com.lendwise.iam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
    @created 2/12/2026 1:24 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String authToken;
    private String userEmail;
    private String userProfilePictureUrl;
    private String role;
    private String kycStatus;
}
