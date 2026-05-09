package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "{validation.auth.refresh_token.not_blank}")
        String refreshToken
) {}
