package com.example.nevera.entity;

import com.example.nevera.common.enums.IngredientStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "savings_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SavingsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    // CONSUMED(구조) or WASTED(폐기)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "text")
    private IngredientStatus status;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        this.recordedAt = OffsetDateTime.now();
    }
}
