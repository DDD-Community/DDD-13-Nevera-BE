package com.example.nevera.dto.notification;

import com.example.nevera.entity.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        String deeplink,
        String type,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Seoul")
        OffsetDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getDeeplink(),
                notification.getType(),
                notification.getCreatedAt()
        );
    }
}
