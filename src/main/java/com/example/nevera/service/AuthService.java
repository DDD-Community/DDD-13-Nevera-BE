package com.example.nevera.service;

import com.example.nevera.common.enums.MemberRole;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.common.google.GoogleTokenVerifier;
import com.example.nevera.common.google.GoogleUserInfo;
import com.example.nevera.common.jwt.JwtProvider;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.entity.MemberEntity;
import com.example.nevera.entity.TokenEntity;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthTokenResponse googleLogin(String idToken, String deviceId) {
        GoogleUserInfo userInfo = googleTokenVerifier.verify(idToken);
        log.info(String.valueOf(userInfo));
        MemberEntity member = findOrCreateMember(userInfo);
        log.info(String.valueOf(member));

        String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getEmail(), member.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId());

        TokenEntity token = tokenRepository.findByMemberAndDeviceId(member, deviceId)
                .orElseGet(() -> TokenEntity.builder()
                        .member(member)
                        .deviceId(deviceId)
                        .refreshToken(refreshToken)
                        .build());
        token.updateRefreshToken(refreshToken);
        tokenRepository.save(token);

        return new AuthTokenResponse(accessToken, refreshToken);
    }

    public AuthTokenResponse refresh(String refreshToken) {
        jwtProvider.parseToken(refreshToken);

        TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        MemberEntity member = tokenEntity.getMember();

        String newAccessToken = jwtProvider.generateAccessToken(member.getId(), member.getEmail(), member.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(member.getId());

        tokenEntity.updateRefreshToken(newRefreshToken);

        return new AuthTokenResponse(newAccessToken, newRefreshToken);
    }

    private MemberEntity findOrCreateMember(GoogleUserInfo info) {
        return memberRepository.findByEmail(info.email())
                .orElseGet(() -> memberRepository.save(
                        MemberEntity.builder()
                                .email(info.email())
                                .name(info.name())
                                .provider("google")
                                .status("ACTIVE")
                                .role(MemberRole.USER)
                                .build()
                ));
    }

}
