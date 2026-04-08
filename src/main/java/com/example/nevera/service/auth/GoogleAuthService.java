package com.example.nevera.service.auth;

import com.example.nevera.common.enums.MemberRole;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.dto.auth.GoogleUserInfo;
import com.example.nevera.entity.Member;
import com.example.nevera.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleAuthService {

    private final MemberRepository memberRepository;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtTokenService jwtTokenService;

    public AuthTokenResponse googleLogin(String idToken) {
        GoogleUserInfo userInfo = googleTokenVerifier.verify(idToken);
        Member member = findOrCreateMember(userInfo);
        return jwtTokenService.issueTokens(member);
    }

    private Member findOrCreateMember(GoogleUserInfo info) {
        return memberRepository.findByEmail(info.email())
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .email(info.email())
                                .name(info.name())
                                .provider("google")
                                .status("ACTIVE")
                                .role(MemberRole.USER)
                                .build()
                ));
    }
}
