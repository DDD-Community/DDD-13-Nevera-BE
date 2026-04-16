package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record InventoryRequest(
        @NotBlank(message = "재료 이름은 필수 입력 값입니다.")
        String name,
        @NotNull(message = "카테고리는 필수 입력 값입니다.")
        Category category,
        @NotNull(message = "보관 장소는 필수 입력 값입니다.")
        StorageLocation location,
        @NotNull(message = "수량은 필수 입력 값입니다.")
        Integer quantity,
        @NotNull(message = "단위는 필수 입력 값입니다.")
        IngredientUnit unit,
        @NotNull(message = "유통기한은 필수 입력 값입니다.")
        OffsetDateTime expirationDate,
        OffsetDateTime useBy,
        @NotNull(message = "재료 상태는 필수 입력 값입니다.")
        IngredientStatus status,
        @NotNull(message = "구매 비용은 필수 입력 값입니다.")
        Integer cost
) {}
