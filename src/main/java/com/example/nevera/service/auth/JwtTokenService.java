package com.example.nevera.service.auth;

import com.example.nevera.common.jwt.JwtProvider;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.Token;
import com.example.nevera.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtTokenService {

    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    public AuthTokenResponse issueTokens(Member member) {
        String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getEmail(), member.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId());

        Token token = tokenRepository.findByMember(member)
                .orElseGet(() -> Token.builder()
                        .member(member)
                        .refreshToken(refreshToken)
                        .build());
        token.updateRefreshToken(refreshToken);
        tokenRepository.save(token);

        return new AuthTokenResponse(accessToken, refreshToken);
    }
}
