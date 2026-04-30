package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;

public record OcrRefineResponse(
        String name,
        Category category,
        StorageLocation location,
        Integer quantity,
        IngredientUnit unit,
        Integer cost
) {}