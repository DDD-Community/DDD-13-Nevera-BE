package com.example.nevera.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
        @NotBlank String token
) {}
