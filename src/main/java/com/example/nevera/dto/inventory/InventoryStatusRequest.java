package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.IngredientStatus;
import jakarta.validation.constraints.NotNull;

public record InventoryStatusRequest(
        @NotNull(message = "재료 상태는 필수 입력 값입니다.")
        IngredientStatus status
) {}
