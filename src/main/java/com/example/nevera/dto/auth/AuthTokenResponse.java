package com.example.nevera.dto.auth;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken
) {}
