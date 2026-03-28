package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.common.jwt.JwtProvider;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.entity.MemberEntity;
import com.example.nevera.entity.TokenEntity;
import com.example.nevera.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;

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

}
