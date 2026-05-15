package com.example.nevera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(nullable = false, columnDefinition = "text")
    private String deeplink;

    @Column(nullable = false, columnDefinition = "text")
    private String type;

    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}
