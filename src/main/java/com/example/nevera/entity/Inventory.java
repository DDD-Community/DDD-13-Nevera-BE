package com.example.nevera.entity;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "text")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "text")
    @Builder.Default
    private StorageLocation location = StorageLocation.FRIDGE;

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "text")
    @Builder.Default
    private IngredientUnit unit = IngredientUnit.NONE;

    @Column(name = "expiration_date")
    private OffsetDateTime expirationDate;

    @Column(name = "use_by")
    private OffsetDateTime useBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "text")
    @Builder.Default
    private IngredientStatus status = IngredientStatus.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private int cost = 0;

    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }

    public void update(String name, Category category, StorageLocation location,
                       int quantity, IngredientUnit unit, OffsetDateTime expirationDate,
                       OffsetDateTime useBy, IngredientStatus status, int cost) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
        this.useBy = useBy;
        this.status = status;
        this.cost = cost;
    }

    // TODO: 냉동 보관으로 변경 시 유통기한, 소비기한 늘리는 setter 따로 작성할지 말지 고민
}
