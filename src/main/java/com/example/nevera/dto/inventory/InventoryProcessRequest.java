package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.IngredientStatus;
import jakarta.validation.constraints.NotNull;

public record InventoryProcessRequest(
        @NotNull IngredientStatus status,
        int ratio
) {
}
