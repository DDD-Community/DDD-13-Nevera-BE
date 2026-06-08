package com.example.nevera.repository;


import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.entity.Inventory;
import com.example.nevera.dto.FridgeStatus.LocationCountDto;
import com.example.nevera.dto.FridgeStatus.ExpiryCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FridgeStatusRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findAllByMemberId(Long memberId);
    List<Inventory> findAllByMemberIdAndStatus(Long memberId, IngredientStatus status);
}