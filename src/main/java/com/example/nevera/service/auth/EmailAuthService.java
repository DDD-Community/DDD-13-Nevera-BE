package com.example.nevera.service.auth;


import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.entity.EmailAuth;
import com.example.nevera.repository.EmailAuthRepository;
import com.example.nevera.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final JavaMailSender mailSender; // 메일 발송용

    @Transactional
    public void sendAuthCode(String email) {
        // 1. 중복 가입 확인

        // 1. Swagger에서 도대체 어떤 글자를 보냈는지 확인
        System.out.println("====== 검증할 이메일: [" + email + "] ======");

        // 2. 이메일 존재 여부 쿼리 결과 확인
        boolean isExist = memberRepository.existsByEmail(email);
        System.out.println("====== DB 조회 결과: " + isExist + " ======");
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 인증 번호 생성
        String authCode = String.valueOf((int)(Math.random() * 899999) + 100000);
        OffsetDateTime expirationTime = OffsetDateTime.now().plusMinutes(3);

        // 3. DB 저장 (EmailAuth 테이블 UPSERT)
        // JPA의 save()는 @Id가 없거나 기존 데이터가 있으면 Update, 없으면 Insert 수행
        EmailAuth emailAuth = emailAuthRepository.findByEmail(email)
                .map(existingAuth -> {
                    existingAuth.updateCode(authCode, expirationTime);
                    return existingAuth;
                })
                .orElse(new EmailAuth(email, authCode, expirationTime));

        emailAuthRepository.save(emailAuth);

        // 4. 메일 발송
        try {
            sendHtmlEmail(email, authCode);
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.MAIL_SEND_ERROR);
        }
    }

    private void sendHtmlEmail(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("[Nevera] 회원가입 인증 번호 안내");

        // HTML 본문 작성 (직접 태그를 넣거나 템플릿 엔진 사용 가능)
        String htmlContent = " <div style='font-family: Arial, sans-serif; max-width: 600px; border: 1px solid #eee; padding: 20px;'>" +
                " <h2 style='color: #2D3436;'>회원가입 인증</h2>" +
                " <p>안녕하세요! 아래의 인증 번호를 입력하여 가입을 완료해 주세요.</p>" +
                " <div style='background-color: #F9F9F9; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #0984E3;'>" +
                code +
                " </div>" +
                " <p style='font-size: 12px; color: #636E72;'>본 번호는 5분간 유효합니다.</p>" +
                " </div>";

        helper.setText(htmlContent, true);
        helper.setFrom("tjtpfks@gmail.com");
        mailSender.send(message);
    }

    @Transactional
    public void verifyCode(String email, String authCode) {
        // 1. DB에서 해당 이메일의 인증 데이터 조회
        EmailAuth auth = emailAuthRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_NOT_FOUND));

        // 2. 만료 시간 확인
        if (auth.getExpirationTime().isBefore(OffsetDateTime.now())) {
            throw new BusinessException(ErrorCode.EXPIRED_AUTH_CODE);
        }

        // 3. 인증 번호 일치 여부 확인
        if (!auth.getAuthCode().equals(authCode)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_CODE);
        }

        // 4. 인증 완료 처리 (상태 변경)
        auth.markAsVerified();

    }
}
