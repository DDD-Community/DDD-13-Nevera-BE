package com.example.nevera.repository;

import com.example.nevera.entity.Member;
import com.example.nevera.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);
    Optional<Token> findByMemberAndDeviceId(Member member, String deviceId);
    void deleteByMemberAndDeviceId(Member member, String deviceId);
    void deleteByMember(Member member);

}
