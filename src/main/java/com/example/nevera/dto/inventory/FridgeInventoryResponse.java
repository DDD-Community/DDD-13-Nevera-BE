package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.entity.Inventory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public record FridgeInventoryResponse (

    Long id,
    String name,
    Category category,
    StorageLocation location,
    int quantity,
    OffsetDateTime expirationDate,
    int dDay,
    int cost,
    OffsetDateTime createdAt
) {
        public static FridgeInventoryResponse from(Inventory inventory) {
            long daysBetween = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    inventory.getExpirationDate().toLocalDate()
            );
            return new FridgeInventoryResponse(
                    inventory.getId(),
                    inventory.getName(),
                    inventory.getCategory(),
                    inventory.getLocation(),
                    inventory.getQuantity(),
                    inventory.getExpirationDate(),
                    (int) daysBetween,
                    inventory.getCost(),
                    inventory.getCreatedAt()
            );
        }
    }


