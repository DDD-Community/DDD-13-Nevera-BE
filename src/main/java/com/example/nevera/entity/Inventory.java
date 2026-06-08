package com.example.nevera.entity;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.dto.inventory.InventoryUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateStatus(IngredientStatus status) {
        this.status = status;
    }

    public void update(String name, Category category, StorageLocation location,
                       int quantity, OffsetDateTime expirationDate, int cost) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.cost = cost;
    }

    // 💡 기존에 있던 일반 수정 메서드 (그대로 두세요)
    public void updateDetails(InventoryUpdateRequest requestDto) {
        this.name = requestDto.name();
        this.category = requestDto.category();
        this.location = requestDto.location();
        this.quantity = requestDto.quantity();
        this.expirationDate = requestDto.expirationDate();
        this.cost = requestDto.cost();
        this.status = requestDto.ingredientStatus();
    }

    public void updateDetailsWithRemainingCost(InventoryUpdateRequest requestDto, int newCost) {
        this.name = requestDto.name();
        this.category = requestDto.category();
        this.location = requestDto.location();
        this.quantity = requestDto.quantity();
        this.expirationDate = requestDto.expirationDate();
        this.cost = newCost;
        this.status = requestDto.ingredientStatus();
    }

    // TODO: 냉동 보관으로 변경 시 유통기한, 소비기한 늘리는 setter 따로 작성할지 말지 고민
}
