package com.example.nevera.repository;

import com.example.nevera.entity.WishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishRepository extends JpaRepository<WishEntity, Long> {

    Optional<WishEntity> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<WishEntity> findByIdAndMemberId(Long id, Long memberId);

    void deleteAllByMemberId(Long memberId);
}
