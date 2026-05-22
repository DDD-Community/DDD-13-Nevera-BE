package com.example.nevera.dto.mypage;

import com.example.nevera.entity.Member;

public record NotificationSettingResponse(
        int notificationHour,
        int notificationMinute
) {
    public static NotificationSettingResponse from(Member member) {
        return new NotificationSettingResponse(
                member.getNotificationHour(),
                member.getNotificationMinute()
        );
    }
}
