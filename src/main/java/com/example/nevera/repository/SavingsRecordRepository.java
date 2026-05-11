package com.example.nevera.repository;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.entity.SavingsRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface SavingsRecordRepository extends JpaRepository<SavingsRecord, Long> {

    void deleteByInventoryId(Long inventoryId);

    @Query("SELECT s FROM SavingsRecord s JOIN FETCH s.inventory WHERE s.member.id = :memberId AND s.status = :status ORDER BY s.recordedAt DESC")
    List<SavingsRecord> findByMemberIdAndStatus(
            @Param("memberId") Long memberId,
            @Param("status") IngredientStatus status,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(s.inventory.cost), 0) FROM SavingsRecord s WHERE s.member.id = :memberId AND s.status = :status AND s.recordedAt >= :from AND s.recordedAt < :to")
    int sumCostByMemberIdAndStatusAndPeriod(
            @Param("memberId") Long memberId,
            @Param("status") IngredientStatus status,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
