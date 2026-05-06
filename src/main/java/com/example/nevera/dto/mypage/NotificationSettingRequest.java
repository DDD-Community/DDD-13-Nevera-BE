package com.example.nevera.dto.mypage;

import jakarta.validation.constraints.NotNull;

public record NotificationSettingRequest(
        @NotNull(message = "알림 수신 여부는 필수 입력 값입니다.")
        Boolean notificationEnabled
) {}