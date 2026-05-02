package com.example.nevera.repository;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findAllByMemberId(Long memberId);

    List<Inventory> findAllByMemberIdAndStatus(Long memberId, IngredientStatus status);

    List<Inventory> findAllByMemberIdAndStatusOrderByUpdatedAtDesc(Long memberId, IngredientStatus status);

    void deleteAllByMemberId(Long memberId);
}
