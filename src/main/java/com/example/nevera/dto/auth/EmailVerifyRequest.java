package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailVerifyRequest(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String email,

        @NotBlank(message = "인증 코드는 필수 입력 값입니다.")
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
