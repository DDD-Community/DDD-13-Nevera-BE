package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.IngredientStatus;

public record InventoryProcessResponse(
        Long inventoryId,
        IngredientStatus processedStatus,
        int processedRatio,
        int processedAmount,
        int consumedRatio,
        int wastedRatio,
        int remainingRatio,
        int remainingAmount,
        IngredientStatus inventoryStatus,
        boolean completed
) {
}
