package com.example.nevera.dto.inventory;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.StorageLocation;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record InventoryUpdateRequest (String name,
                                      Category category,
                                      StorageLocation location,
                                      int quantity,
                                      OffsetDateTime expirationDate,
                                      int cost,
                                      @JsonProperty("ingredientStatus")
                                      IngredientStatus ingredientStatus,
                                      int ratio
) {}