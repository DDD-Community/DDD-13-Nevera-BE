package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.IngredientStatus;
import jakarta.validation.constraints.NotNull;

public record InventoryStatusRequest(
        @NotNull(message = "{validation.inventory.status.not_null}")
        IngredientStatus status
) {}
