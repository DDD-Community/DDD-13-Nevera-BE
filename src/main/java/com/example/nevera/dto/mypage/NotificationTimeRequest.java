package com.example.nevera.dto.mypage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record NotificationTimeRequest(
        @NotNull(message = "알림 시간(시)은 필수 입력 값입니다.")
        @Min(value = 0, message = "알림 시간(시)은 0 이상이어야 합니다.")
        @Max(value = 23, message = "알림 시간(시)은 23 이하이어야 합니다.")
        Integer notificationHour,

        @NotNull(message = "알림 시간(분)은 필수 입력 값입니다.")
        Integer notificationMinute
) {}