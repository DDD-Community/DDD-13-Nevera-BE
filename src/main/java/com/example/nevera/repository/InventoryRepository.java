package com.example.nevera.repository;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.entity.Inventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findAllByMemberId(Long memberId);

    List<Inventory> findAllByMemberIdAndStatus(Long memberId, IngredientStatus status);

    List<Inventory> findAllByMemberIdAndStatusOrderByUpdatedAtDesc(Long memberId, IngredientStatus status);

    void deleteAllByMemberId(Long memberId);

    List<Inventory> findAllByStatusAndExpirationDateGreaterThanEqualAndExpirationDateLessThan(
            IngredientStatus status,
            OffsetDateTime startOfDay,
            OffsetDateTime endOfDay
    );

    @Query("SELECT i FROM Inventory i " +
            "WHERE (:storageLocation IS NULL OR i.location = :storageLocation) " +
            "AND (:category IS NULL OR i.category = :category)")
    Slice<Inventory> findIngredientsByFilters(
            @Param("storageLocation") StorageLocation storageLocation,
            @Param("category") Category category,
            Pageable pageable
    );
}
