package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailVerifyRequest(
        @NotBlank(message = "{validation.auth.email.not_blank}")
        String email,

        @NotBlank(message = "{validation.auth.auth_code.not_blank}")
        String authCode
) {


    @Override
    public String email() {
        return email;
    }

    @Override
    public String authCode() {
        return authCode;
    }
}
