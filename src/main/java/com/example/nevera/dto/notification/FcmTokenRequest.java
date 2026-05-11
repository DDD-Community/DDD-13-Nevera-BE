package com.example.nevera.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
        @NotBlank(message = "{validation.notification.token.not_blank}")
        String token
) {}
