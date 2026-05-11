package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record InventoryRequest(
        @NotBlank(message = "{validation.inventory.name.not_blank}")
        String name,
        @NotNull(message = "{validation.inventory.category.not_null}")
        Category category,
        @NotNull(message = "{validation.inventory.location.not_null}")
        StorageLocation location,
        @NotNull(message = "{validation.inventory.quantity.not_null}")
        Integer quantity,
        @NotNull(message = "{validation.inventory.unit.not_null}")
        IngredientUnit unit,
        @NotNull(message = "{validation.inventory.expiration_date.not_null}")
        OffsetDateTime expirationDate,
        OffsetDateTime useBy,
        @NotNull(message = "{validation.inventory.status.not_null}")
        IngredientStatus status,
        @NotNull(message = "{validation.inventory.cost.not_null}")
        Integer cost
) {}
