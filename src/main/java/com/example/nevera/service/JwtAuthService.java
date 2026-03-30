package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.common.jwt.JwtProvider;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.Token;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtAuthService {

    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

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

    public void logout(Long memberId, String deviceId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        tokenRepository.deleteByMemberAndDeviceId(member, deviceId);
    }

    public void deleteAccount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        tokenRepository.deleteByMember(member);
        memberRepository.delete(member);
    }

}
