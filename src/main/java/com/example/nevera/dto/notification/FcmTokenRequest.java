package com.example.nevera.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
        @NotBlank(message = "토큰은 필수 입력값입니다.")
        String token
) {}
