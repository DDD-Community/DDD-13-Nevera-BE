package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.entity.Inventory;
import com.example.nevera.entity.SavingsRecord;

public record ConsumedWastedResponse(
        Long id,
        String name,
        Category category,
        String categoryDisplayName,
        int quantity,
        int cost
) {
    public static ConsumedWastedResponse from(SavingsRecord record) {
        Inventory inventory = record.getInventory();
        return new ConsumedWastedResponse(
                inventory.getId(),
                inventory.getName(),
                inventory.getCategory(),
                inventory.getCategory().getDisplayName(),
                inventory.getQuantity(),
                record.getAmount()
        );
    }
}
