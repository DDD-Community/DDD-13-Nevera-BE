package com.example.nevera.service;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.inventory.InventoryRequest;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.entity.Inventory;
import com.example.nevera.entity.Member;
import com.example.nevera.repository.InventoryRepository;
import com.example.nevera.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public InventoryResponse create(Long memberId, InventoryRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Inventory inventory = Inventory.builder()
                .member(member)
                .name(request.name())
                .category(request.category())
                .location(request.location())
                .quantity(request.quantity())
                .unit(request.unit())
                .expirationDate(request.expirationDate())
                .useBy(request.useBy())
                .status(request.status())
                .cost(request.cost())
                .build();

        return InventoryResponse.from(inventoryRepository.save(inventory));
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllActive(Long memberId) {
        return inventoryRepository.findAllByMemberIdAndStatus(memberId, IngredientStatus.ACTIVE)
                .stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllConsumed(Long memberId) {
        return inventoryRepository.findAllByMemberIdAndStatus(memberId, IngredientStatus.CONSUMED)
                .stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllWasted(Long memberId) {
        return inventoryRepository.findAllByMemberIdAndStatus(memberId, IngredientStatus.WASTED)
                .stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getOne(Long memberId, Long inventoryId) {
        Inventory inventory = findAndValidate(memberId, inventoryId);
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse update(Long memberId, Long inventoryId, InventoryRequest request) {
        Inventory inventory = findAndValidate(memberId, inventoryId);

        inventory.update(
                request.name(),
                request.category(),
                request.location(),
                request.quantity(),
                request.unit(),
                request.expirationDate(),
                request.useBy(),
                request.status(),
                request.cost()
        );

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public void delete(Long memberId, Long inventoryId) {
        Inventory inventory = findAndValidate(memberId, inventoryId);
        inventoryRepository.delete(inventory);
    }

    private Inventory findAndValidate(Long memberId, Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
        if (!inventory.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.INVENTORY_FORBIDDEN);
        }
        return inventory;
    }
}
