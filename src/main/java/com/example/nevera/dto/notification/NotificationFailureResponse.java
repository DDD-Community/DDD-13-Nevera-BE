package com.example.nevera.dto.notification;

import com.example.nevera.entity.NotificationFailure;

import java.time.format.DateTimeFormatter;

public record NotificationFailureResponse(
        Long id,
        Long memberId,
        Long inventoryId,
        Long notificationId,
        String reason,
        int retryCount,
        String failedAt
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static NotificationFailureResponse from(NotificationFailure failure) {
        return new NotificationFailureResponse(
                failure.getId(),
                failure.getMember().getId(),
                failure.getInventory().getId(),
                failure.getNotification().getId(),
                failure.getReason(),
                failure.getRetryCount(),
                failure.getFailedAt().format(FORMATTER)
        );
    }
}
