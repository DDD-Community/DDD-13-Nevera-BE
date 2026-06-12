package com.example.nevera.dto.notification;

import com.example.nevera.entity.NotificationFailure;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record NotificationFailureResponse(
        Long id,
        Long memberId,
        Long inventoryId,
        Long notificationId,
        String reason,
        int retryCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Seoul")
        OffsetDateTime failedAt
) {
    public static NotificationFailureResponse from(NotificationFailure failure) {
        return new NotificationFailureResponse(
                failure.getId(),
                failure.getMember().getId(),
                failure.getInventory().getId(),
                failure.getNotification().getId(),
                failure.getReason(),
                failure.getRetryCount(),
                failure.getFailedAt()
        );
    }
}
