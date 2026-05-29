package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.entity.Inventory;

public record ConsumedWastedResponse(
        Long id,
        String name,
        Category category,
        String categoryDisplayName,
        int quantity,
        int cost
) {
    public static ConsumedWastedResponse from(Inventory inventory) {
        return new ConsumedWastedResponse(
                inventory.getId(),
                inventory.getName(),
                inventory.getCategory(),
                inventory.getCategory().getDisplayName(),
                inventory.getQuantity(),
                inventory.getCost()
        );
    }
}
