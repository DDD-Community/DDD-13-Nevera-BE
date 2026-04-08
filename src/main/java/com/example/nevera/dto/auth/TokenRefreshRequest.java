package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message="refreshToken은 필수 입력값입니다.")
        String refreshToken
) {}
