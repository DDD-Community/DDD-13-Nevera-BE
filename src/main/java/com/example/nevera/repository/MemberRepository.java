package com.example.nevera.repository;

import com.example.nevera.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일 중복 여부 확인
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

}