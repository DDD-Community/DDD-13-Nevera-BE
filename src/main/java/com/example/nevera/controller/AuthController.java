package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.auth.*;
import com.example.nevera.service.auth.EmailAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.service.auth.JwtAuthService;
import com.example.nevera.service.auth.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증 관련 API (회원가입, 이메일 인증 등)") // 컨트롤러 전체 설명
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailAuthService emailAuthService;
    private final JwtAuthService authService;
    private final GoogleAuthService googleAuthService;

    @Operation(summary = "이메일 전송", description = "가입 가능한 이메일 검증 후 이메일 전송")
    @PostMapping("/email-request")
    public ApiResponse<?> requestEmailAuth(@RequestBody EmailRequest request) {
        emailAuthService.sendAuthCode(request.email());
        return ApiResponse.success(Map.of("message", "인증 번호가 발송되었습니다."));
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증 번호 확인")
    @PostMapping("/email-verify")
    public ApiResponse<?> verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        emailAuthService.verifyCode(request.email(), request.authCode());
        return ApiResponse.success(Map.of("message", "인증에 성공하였습니다"));
    }

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<?> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ApiResponse.success(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/login")
    public AuthTokenResponse emailLogin(@Valid @RequestBody LoginRequest request) {

        return authService.emailLogin(request);
    }

    @Operation(summary = "구글 로그인 / 회원가입", description = "구글 로그인 / 회원가입")
    @PostMapping("/google")
    public AuthTokenResponse googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return googleAuthService.googleLogin(request.idToken());
    }

    @Operation(summary = "refresh 토큰 재발급", description = "refresh 토큰 재발급")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh")
    public AuthTokenResponse refresh(
            @Valid @RequestBody(required = false) TokenRefreshRequest body,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String token = resolveToken(body, authHeader);
        return authService.refresh(token);
    }

    @Operation(summary = "로그아웃", description = "해당 멤버의 refresh 토큰 삭제")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ApiResponse<?> logout(@AuthenticationPrincipal Long memberId) {
        authService.logout(memberId);
        return ApiResponse.success(Map.of("message", "로그아웃이 완료되었습니다."));
    }

    @Operation(summary = "회원 탈퇴", description = "모든 기기의 refresh 토큰 삭제 및 회원 정보 삭제")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/withdraw")
    public ApiResponse<?> withdraw(@AuthenticationPrincipal Long memberId) {
        authService.deleteAccount(memberId);
        return ApiResponse.success(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }

    private String resolveToken(TokenRefreshRequest body, String authHeader) {
        if (body != null && body.refreshToken() != null) return body.refreshToken();
        throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

}