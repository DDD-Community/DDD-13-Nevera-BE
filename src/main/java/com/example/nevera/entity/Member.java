package com.example.nevera.entity;


import com.example.nevera.common.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "text")
    private String email;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String password;

    @Column(columnDefinition = "text")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(columnDefinition = "text")
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "text")
    @Builder.Default
    private MemberRole role = MemberRole.USER;


    @Column(nullable = false, columnDefinition = "text")
    @Builder.Default
    private String nickname = "닉네임설정";

    @Column(name = "profile_image_url", nullable = false, columnDefinition = "text")
    @Builder.Default
    private String profileImageUrl = "/images/default_profile.png";

    @Column(name = "notification_enabled", nullable = false)
    @Builder.Default
    private boolean notificationEnabled = true;

    @Column(name = "notification_hour", nullable = false)
    @Builder.Default
    private int notificationHour = 18;

    @Column(name = "notification_minute", nullable = false)
    @Builder.Default
    private int notificationMinute = 0;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }

    public void updateNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public void updateNotificationTime(int notificationHour, int notificationMinute) {
        this.notificationHour = notificationHour;
        this.notificationMinute = notificationMinute;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}
