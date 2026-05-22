package com.example.nevera.dto.notification;

import com.example.nevera.entity.Notification;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        String deeplink,
        String type,
        String createdAt
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getDeeplink(),
                notification.getType(),
                notification.getCreatedAt().format(FORMATTER)
        );
    }
}
