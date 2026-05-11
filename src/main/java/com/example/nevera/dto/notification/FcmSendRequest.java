package com.example.nevera.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmSendRequest(
        @NotBlank(message = "{validation.notification.title.not_blank}")
        String title,
        @NotBlank(message = "{validation.notification.body.not_blank}")
        String body
) {}
