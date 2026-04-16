package com.example.nevera.service.auth;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.common.jwt.JwtProvider;
import com.example.nevera.dto.auth.LoginRequest;
import com.example.nevera.dto.auth.SignupRequest;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.entity.EmailAuth;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.Token;
import com.example.nevera.repository.EmailAuthRepository;
import com.example.nevera.repository.InventoryRepository;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtAuthService {

    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final EmailAuthRepository emailAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final InventoryRepository inventoryRepository;

    public AuthTokenResponse refresh(String refreshToken) {
        jwtProvider.parseToken(refreshToken);

        Token tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Member member = tokenEntity.getMember();

        String newAccessToken = jwtProvider.generateAccessToken(member.getId(), member.getEmail(), member.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(member.getId());

        tokenEntity.updateRefreshToken(newRefreshToken);

        return new AuthTokenResponse(newAccessToken, newRefreshToken);
    }

    public void signup(SignupRequest request) {
        // 1. EmailAuth 테이블에서 is_verified가 true인지 최종 체크 (프론트 조작 방지)
        EmailAuth auth = emailAuthRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_NOT_FOUND));

        if (!auth.isVerified()) {
            throw new BusinessException(ErrorCode.UNVERIFIED_EMAIL);
        }
        if (!request.isPasswordMatch()) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
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

    public AuthTokenResponse emailLogin(LoginRequest request) {
        // 1. 이메일 존재 확인
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. 토큰 발급 및 저장
        return jwtTokenService.issueTokens(member);
    }

    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        tokenRepository.deleteByMember(member);
    }

    public void deleteAccount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        tokenRepository.deleteByMember(member);
        inventoryRepository.deleteAllByMemberId(memberId);
        memberRepository.delete(member);
    }
}