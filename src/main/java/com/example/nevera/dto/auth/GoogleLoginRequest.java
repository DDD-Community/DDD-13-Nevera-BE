package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "{validation.auth.id_token.not_blank}")
        String idToken
) {}
