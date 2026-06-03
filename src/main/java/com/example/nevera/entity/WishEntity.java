package com.example.nevera.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "wish")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "achieved_at")
    private OffsetDateTime achievedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }

    public boolean isAchieved() {
        return this.achievedAt != null;
    }

    public void achieve() {
        this.achievedAt = OffsetDateTime.now();
    }

    public void update(String name, Long amount) {
        this.name = name;
        this.amount = amount;
    }
}
