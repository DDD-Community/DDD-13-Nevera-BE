package com.example.nevera.dto.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FcmSendRequest(
        @NotNull(message = "{validation.notification.data.not_null}")
        @Valid
        Data data
) {
    public record Data(
            String id,
            @NotBlank(message = "{validation.notification.title.not_blank}")
            String title,
            @NotBlank(message = "{validation.notification.body.not_blank}")
            String message,
            String createdAt,
            String deeplink,
            String type
    ) {}
}
