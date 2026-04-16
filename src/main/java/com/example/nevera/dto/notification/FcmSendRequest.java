package com.example.nevera.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmSendRequest(
        @NotBlank String title,
        @NotBlank String body
) {}
