package com.example.nevera.dto.mypage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record NotificationTimeRequest(
        @NotNull(message = "{validation.mypage.notification_hour.not_null}")
        @Min(value = 0, message = "{validation.mypage.notification_hour.min}")
        @Max(value = 23, message = "{validation.mypage.notification_hour.max}")
        Integer notificationHour,

        @NotNull(message = "{validation.mypage.notification_minute.not_null}")
        Integer notificationMinute
) {}