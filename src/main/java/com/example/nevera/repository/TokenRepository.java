package com.example.nevera.repository;

import com.example.nevera.entity.MemberEntity;
import com.example.nevera.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByRefreshToken(String refreshToken);
    Optional<TokenEntity> findByMemberAndDeviceId(MemberEntity member, String deviceId);
}
