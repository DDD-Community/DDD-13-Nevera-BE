package com.example.nevera.service.googleAuth;

import com.example.nevera.common.enums.MemberRole;
import com.example.nevera.common.jwt.JwtProvider;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.dto.auth.GoogleUserInfo;
import com.example.nevera.entity.MemberEntity;
import com.example.nevera.entity.TokenEntity;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleAuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthTokenResponse googleLogin(String idToken, String deviceId) {
        GoogleUserInfo userInfo = googleTokenVerifier.verify(idToken);
        MemberEntity member = findOrCreateMember(userInfo);

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
