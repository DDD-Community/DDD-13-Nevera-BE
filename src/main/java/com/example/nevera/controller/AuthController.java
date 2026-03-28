package com.example.nevera.controller;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.auth.AuthTokenResponse;
import com.example.nevera.dto.auth.GoogleLoginRequest;
import com.example.nevera.dto.auth.TokenRefreshRequest;
import com.example.nevera.service.AuthService;
import com.example.nevera.service.googleAuth.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public AuthTokenResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return googleAuthService.googleLogin(request.idToken(), request.deviceId());
    }

    @PostMapping("/refresh")
    public AuthTokenResponse refresh(
            @RequestBody(required = false) TokenRefreshRequest body,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String token = resolveToken(body, authHeader);
        return authService.refresh(token);
    }

    private String resolveToken(TokenRefreshRequest body, String authHeader) {
        if (body != null && body.refreshToken() != null) return body.refreshToken();
        throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
}
