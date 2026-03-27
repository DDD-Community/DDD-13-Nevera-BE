package com.example.nevera.repository;

import com.example.nevera.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    // 이메일로 기존 인증 정보 조회
    Optional<EmailAuth> findByEmail(String email);

    // 회원가입 완료 후 해당 이메일의 모든 인증 데이터를 삭제
    void deleteByEmail(String email);
}