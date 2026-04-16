package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.entity.Inventory;

import java.time.OffsetDateTime;

public record InventoryResponse(
        Long id,
        String name,
        Category category,
        StorageLocation location,
        int quantity,
        IngredientUnit unit,
        OffsetDateTime expirationDate,
        OffsetDateTime useBy,
        IngredientStatus status,
        int cost,
        OffsetDateTime createdAt
) {
    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getName(),
                inventory.getCategory(),
                inventory.getLocation(),
                inventory.getQuantity(),
                inventory.getUnit(),
                inventory.getExpirationDate(),
                inventory.getUseBy(),
                inventory.getStatus(),
                inventory.getCost(),
                inventory.getCreatedAt()
        );
    }
}
