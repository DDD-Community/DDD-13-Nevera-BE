package com.example.nevera.service;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.inventory.FridgeInventoryResponse;
import com.example.nevera.dto.inventory.InventoryProcessRequest;
import com.example.nevera.dto.inventory.InventoryProcessResponse;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.dto.inventory.InventoryUpdateRequest;
import com.example.nevera.entity.Inventory;
import com.example.nevera.entity.SavingsRecord;
import com.example.nevera.repository.InventoryRepository;
import com.example.nevera.repository.SavingsRecordRepository;
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
    private final SavingsRecordRepository savingsRecordRepository;

    @Transactional(readOnly = true)
    public Slice<FridgeInventoryResponse> getIngredients(
            Long memberId,
            StorageLocation storageLocation,
            Category category,
            Pageable pageable
    ) {
        return inventoryRepository.findIngredientsByFilters(memberId, storageLocation, category, pageable)
                .map(FridgeInventoryResponse::from);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getIngredientDetail(Long memberId, Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
        validateOwner(memberId, inventory);
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse updateIngredient(
            Long memberId,
            Long inventoryId,
            InventoryUpdateRequest request
    ) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
        validateOwner(memberId, inventory);

        boolean hasProcessRecord = savingsRecordRepository.existsByInventoryId(inventoryId);
        if (hasProcessRecord && inventory.getCost() != request.cost()) {
            throw new BusinessException(ErrorCode.INVENTORY_COST_CHANGE_NOT_ALLOWED);
        }

        inventory.updateDetails(request);
        if (!hasProcessRecord) {
            inventory.updateCostBeforeProcessing(request.cost());
        }
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryProcessResponse processIngredient(
            Long memberId,
            Long inventoryId,
            InventoryProcessRequest request
    ) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
        validateOwner(memberId, inventory);
        validateProcessRequest(request);

        if (inventory.isCompleted()) {
            throw new BusinessException(ErrorCode.INVENTORY_ALREADY_COMPLETED);
        }

        int currentRatio = savingsRecordRepository.sumRatioByInventoryId(inventoryId);
        int totalRatio = currentRatio + request.ratio();
        if (totalRatio > 100) {
            throw new BusinessException(ErrorCode.PROCESS_RATIO_EXCEEDED);
        }

        int consumedRatio = savingsRecordRepository.sumRatioByInventoryIdAndStatus(
                inventoryId, IngredientStatus.CONSUMED);
        int wastedRatio = savingsRecordRepository.sumRatioByInventoryIdAndStatus(
                inventoryId, IngredientStatus.WASTED);

        if (request.status() == IngredientStatus.CONSUMED) {
            consumedRatio += request.ratio();
        } else {
            wastedRatio += request.ratio();
        }

        int processedAmount = totalRatio == 100
                ? inventory.getCost()
                : Math.min(
                        inventory.getCost(),
                        roundToTen(inventory.getOriginalCost() * request.ratio() / 100.0)
                );

        savingsRecordRepository.save(SavingsRecord.builder()
                .member(inventory.getMember())
                .inventory(inventory)
                .status(request.status())
                .ratio(request.ratio())
                .amount(processedAmount)
                .build());

        if (totalRatio == 100) {
            inventory.complete(consumedRatio >= wastedRatio
                    ? IngredientStatus.CONSUMED
                    : IngredientStatus.WASTED);
        } else {
            inventory.decreaseCost(processedAmount);
        }

        return new InventoryProcessResponse(
                inventoryId,
                request.status(),
                request.ratio(),
                processedAmount,
                consumedRatio,
                wastedRatio,
                100 - totalRatio,
                inventory.getCost(),
                inventory.getStatus(),
                inventory.isCompleted()
        );
    }

    private void validateOwner(Long memberId, Inventory inventory) {
        if (!inventory.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.INVENTORY_FORBIDDEN);
        }
    }

    private void validateProcessRequest(InventoryProcessRequest request) {
        if (request.status() != IngredientStatus.CONSUMED
                && request.status() != IngredientStatus.WASTED) {
            throw new BusinessException(ErrorCode.INVALID_PROCESS_STATUS);
        }
        if (request.ratio() != 25 && request.ratio() != 50
                && request.ratio() != 75 && request.ratio() != 100) {
            throw new BusinessException(ErrorCode.INVALID_PROCESS_RATIO);
        }
    }

    private int roundToTen(double amount) {
        return (int) (Math.round(amount / 10.0) * 10);
    }
}
