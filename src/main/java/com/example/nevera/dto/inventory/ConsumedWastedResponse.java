package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.entity.Inventory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public record ConsumedWastedResponse(
        Long id,
        String name,
        Long dDay,
        StorageLocation location,
        Category category,
        int quantity,
        IngredientUnit unit,
        int cost
) {
    public static ConsumedWastedResponse from(Inventory inventory) {
        Long dDay = calculateDDay(inventory.getExpirationDate());
        return new ConsumedWastedResponse(
                inventory.getId(),
                inventory.getName(),
                dDay,
                inventory.getLocation(),
                inventory.getCategory(),
                inventory.getQuantity(),
                inventory.getUnit(),
                inventory.getCost()
        );
    }

    private static Long calculateDDay(OffsetDateTime expirationDate) {
        if (expirationDate == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        LocalDate expiry = expirationDate.toLocalDate();
        return ChronoUnit.DAYS.between(today, expiry);
    }
}
