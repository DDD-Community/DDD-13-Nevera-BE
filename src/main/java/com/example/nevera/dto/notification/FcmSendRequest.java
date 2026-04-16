package com.example.nevera.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmSendRequest(
        @NotBlank(message = "알림 제목(title)은 필수 입력값입니다.")
        String title,
        @NotBlank(message = "알림 내용(body)은 필수 입력값입니다.")
        String body
) {}
