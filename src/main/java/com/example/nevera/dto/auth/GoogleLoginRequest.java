package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "idToken은 필수 입력값입니다.")
        String idToken
) {}
