package com.example.nevera.service;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.SortType;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.inventory.FridgeInventoryResponse;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.dto.inventory.InventoryUpdateRequest;
import com.example.nevera.entity.Inventory;
import com.example.nevera.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyFridgeService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public Slice<FridgeInventoryResponse> getIngredients(StorageLocation storageLocation, Category category, Pageable pageable) {
        return inventoryRepository.findIngredientsByFilters(storageLocation, category, pageable)
                .map(FridgeInventoryResponse::from);
    }

    @Transactional
    public InventoryResponse updateIngredient(Long id, InventoryUpdateRequest inventoryUpdateRequest) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
        int ratio = inventoryUpdateRequest.ratio();

        if (ratio != 0 && ratio != 25 && ratio != 50 && ratio != 75 && ratio != 100) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (ratio == 100) {
            inventoryRepository.delete(inventory);
            return null;
        }
        if (ratio > 0 && ratio < 100) {
            double remainingPercent = (100 - ratio) / 100.0;
            int newCost = (int) (Math.round((inventoryUpdateRequest.cost() * remainingPercent) / 10.0) * 10);
            inventory.updateDetailsWithRemainingCost(inventoryUpdateRequest, newCost);
        } else {

            inventory.updateDetails(inventoryUpdateRequest);
        }

        return InventoryResponse.from(inventory);
    }

}

