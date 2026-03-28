package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.LoginRequest;
import com.example.nevera.dto.SignupRequest;
import com.example.nevera.entity.EmailAuth;
import com.example.nevera.entity.Member;
import com.example.nevera.repository.EmailAuthRepository;
import com.example.nevera.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 암호화 도구

    @Transactional
    public void signup(SignupRequest request) {
        // 1. EmailAuth 테이블에서 is_verified가 true인지 최종 체크 (프론트 조작 방지)
        EmailAuth auth = emailAuthRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_NOT_FOUND));

        if (!auth.isVerified()) {
            throw new BusinessException(ErrorCode.UNVERIFIED_EMAIL);
        }

        // 2. 비밀번호 암호화 및 가입 데이터 생성
        String encodedPassword = passwordEncoder.encode(request.password());

        Member newMember = Member.builder()
                .email(request.email())
                .password(encodedPassword)
                .name(request.name())
                .provider("LOCAL")
                .build();

        memberRepository.save(newMember);

        // 3. 인증 데이터 삭제 (가입 성공 후 정리)
        emailAuthRepository.delete(auth);
    }

    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        // 1. 이메일 존재 확인
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. jwt 코드
        // return jwtProvider.createToken(member.getEmail(), member.getRole());

        return "임시_토큰_나중에_교체예정";
    }
}