package com.example.nevera.service;

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
    public Boolean create(Long memberId, List<InventoryRequest> requests) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        for (InventoryRequest request : requests) {
            Inventory inventory = Inventory.builder()
                    .member(member)
                    .name(request.name())
                    .category(request.category())
                    .location(request.location())
                    .quantity(request.quantity())
                    .expirationDate(request.expirationDate())
                    .cost(request.cost())
                    .build();
            inventoryRepository.save(inventory);
        }
        return true;
    }

    @Transactional
    public InventoryResponse update(Long memberId, Long inventoryId, InventoryRequest request) {
        Inventory inventory = findAndValidate(memberId, inventoryId);

        inventory.update(
                request.name(),
                request.category(),
                request.location(),
                request.quantity(),
                request.expirationDate(),
                request.cost()
        );

        return InventoryResponse.from(inventory);
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
