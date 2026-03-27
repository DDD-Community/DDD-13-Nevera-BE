package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;

import com.example.nevera.dto.SignupRequest;
import com.example.nevera.entity.EmailAuth;
import com.example.nevera.entity.Member;
import com.example.nevera.repository.EmailAuthRepository;
import com.example.nevera.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;


    @Test
    @DisplayName("회원가입 시 인증 데이터가 없으면 예외가 발생한다")
    void signup_AuthNotFound() {
        // Given (준비: 새로 만든 record 구조에 맞춰서 데이터 세팅!)
        SignupRequest request = new SignupRequest(
                "test@example.com", "password123", "password123", "테스터"
        );

        // DB에 인증 내역이 아예 없는 상황
        given(emailAuthRepository.findByEmail(request.email())).willReturn(Optional.empty());

        // When & Then (실행 & 검증)
        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("이메일 인증이 완료되지 않은 상태로 가입하면 예외가 발생한다")
    void signup_UnverifiedEmail() {
        // Given (준비)
        SignupRequest request = new SignupRequest(
                "test@example.com", "password123", "password123", "테스터"
        );

        // 데이터는 있지만, 아직 인증(isVerified)이 안 된 상태의 객체
        EmailAuth unverifiedAuth = new EmailAuth(request.email(), "123456", LocalDateTime.now().plusMinutes(3));

        given(emailAuthRepository.findByEmail(request.email())).willReturn(Optional.of(unverifiedAuth));

        // When & Then (실행 & 검증)
        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("이메일 인증이 완료된 상태면 정상적으로 회원가입이 완료되고 인증 데이터는 삭제된다")
    void signup_Success() {
        // Given (준비)
        SignupRequest request = new SignupRequest(
                "test@example.com", "password123", "password123", "테스터"
        );

        // 1. 인증이 완료된 완벽한 상태 만들기
        EmailAuth verifiedAuth = new EmailAuth(request.email(), "123456", LocalDateTime.now().plusMinutes(3));
        verifiedAuth.markAsVerified();

        given(emailAuthRepository.findByEmail(request.email())).willReturn(Optional.of(verifiedAuth));

        // 2. 가짜 암호화 도구가 어떻게 대답할지 설정
        String expectedEncodedPassword = "encoded_password_123";
        given(passwordEncoder.encode(request.password())).willReturn(expectedEncodedPassword);

        // When (실행)
        memberService.signup(request);

        // Then (검증)
        // 1. 회원 정보가 DB에 1번 저장(save)되었는가?
        verify(memberRepository, times(1)).save(any(Member.class));

        // 2. 가입 끝났으니 쓰임을 다한 인증 데이터는 1번 삭제(delete)되었는가?
        verify(emailAuthRepository, times(1)).delete(verifiedAuth);
    }
}