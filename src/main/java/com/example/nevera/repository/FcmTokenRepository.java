package com.example.nevera.repository;

import com.example.nevera.entity.FcmToken;
import com.example.nevera.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByMember(Member member);
    void deleteByMember(Member member);
}
