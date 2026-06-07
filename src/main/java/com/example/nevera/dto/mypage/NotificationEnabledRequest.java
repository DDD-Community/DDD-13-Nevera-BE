package com.example.nevera.dto.mypage;

import jakarta.validation.constraints.NotNull;

public record NotificationEnabledRequest(
        @NotNull(message = "{validation.mypage.notification_enabled.not_null}")
        Boolean notificationEnabled
) {}
