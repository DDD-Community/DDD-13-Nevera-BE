package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.entity.EmailAuth;
import com.example.nevera.repository.EmailAuthRepository;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.service.auth.EmailAuthService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmailAuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @Mock
    private JavaMailSender mailSender;


    @InjectMocks
    private EmailAuthService emailAuthService;

    @Test
    @DisplayName("중복된 이메일로 인증을 요청하면 예외가 발생한다")
    void sendAuthCode_DuplicateEmail() {
        // Given
        String duplicatedEmail = "test@example.com";

        given(memberRepository.existsByEmail(duplicatedEmail)).willReturn(true);

        assertThatThrownBy(() -> emailAuthService.sendAuthCode(duplicatedEmail))
                .isInstanceOf(BusinessException.class);
    }

    @Mock
    private MimeMessage mimeMessage;

    @Test
    @DisplayName("새로운 이메일이면 인증 번호가 정상적으로 생성되고 메일이 발송된다")
    void sendAuthCode_Success() throws Exception {
        // Given
        String newEmail = "new@example.com";
        given(memberRepository.existsByEmail(newEmail)).willReturn(false);


        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // When (실행: 해당 이메일로 인증 번호 전송 시도)
        emailAuthService.sendAuthCode(newEmail);

        // Then (검증: 정상적으로 끝났다면 아래 행동들이 1번씩 실행되었어야 함)

        verify(emailAuthRepository, times(1)).save(any(EmailAuth.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("DB에 이메일 인증 정보가 없으면 예외가 발생한다")
    void verifyCode_AuthNotFound() {
        // Given (준비: 가짜 DB가 '빈 껍데기(Optional.empty())'를 주도록 설정)
        String email = "ghost@example.com";
        String inputCode = "123456";

        given(emailAuthRepository.findByEmail(email)).willReturn(Optional.empty());

        // When & Then (실행 & 검증: 예외가 터지는지 확인!)
        assertThatThrownBy(() -> emailAuthService.verifyCode(email, inputCode))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("인증 시간이 만료된 경우 예외가 발생한다")
    void verifyCode_ExpiredAuthCode() {
        // Given (준비: 이미 시간이 지난 데이터를 DB에서 꺼내온 상황)
        String email = "test@example.com";
        String correctCode = "123456";

        // 핵심! 현재 시간보다 '1분 과거'로 설정해서 이미 만료된 상태로 만듦
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(1);
        EmailAuth expiredAuth = new EmailAuth(email, correctCode, pastTime);

        // DB가 이 만료된 데이터를 반환하도록 조작
        given(emailAuthRepository.findByEmail(email)).willReturn(Optional.of(expiredAuth));

        // When & Then (실행 & 검증: 예외가 빵 터지는지 확인!)
        assertThatThrownBy(() -> emailAuthService.verifyCode(email, correctCode))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("인증 번호가 틀린 경우 예외가 발생한다")
    void verifyCode_InvalidAuthCode() {
        // Given (준비: 시간은 넉넉하지만, 번호가 '123456'인 데이터를 DB에서 꺼내온 상황)
        String email = "test@example.com";
        String correctCode = "123456";
        String wrongCode = "999999"; // 사용자가 엉뚱하게 입력한 번호

        // 시간은 현재 시간보다 3분 미래로 설정해서 안 지나게 만듦
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(3);
        EmailAuth validAuth = new EmailAuth(email, correctCode, futureTime);

        given(emailAuthRepository.findByEmail(email)).willReturn(Optional.of(validAuth));

        // When & Then (실행 & 검증: 사용자가 틀린 번호(wrongCode)를 넣었을 때 예외 터지는지 확인!)
        assertThatThrownBy(() -> emailAuthService.verifyCode(email, wrongCode))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("인증 시간이 안 지났고 번호도 맞으면 예외 없이 인증이 완료된다")
    void verifyCode_Success() {
        // Given (준비)
        String email = "test@example.com";
        String correctCode = "123456";
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(3);

        EmailAuth validAuth = new EmailAuth(email, correctCode, futureTime);
        given(emailAuthRepository.findByEmail(email)).willReturn(Optional.of(validAuth));

        // When (실행)
        // 💡 이번엔 assertThatCode(...)를 빼고 그냥 깔끔하게 실행만 시킵니다.
        emailAuthService.verifyCode(email, correctCode);

        // Then (검증)
        // 💡 markAsVerified()가 잘 작동해서 isVerified 값이 true로 바뀌었는지 직접 확인!
        assertThat(validAuth.isVerified()).isTrue();
    }
}