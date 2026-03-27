package com.example.nevera.controller;

import com.example.nevera.dto.EmailVerifyRequest;
import com.example.nevera.service.EmailAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API (회원가입, 이메일 인증 등)") // 컨트롤러 전체 설명
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final EmailAuthService emailAuthService;

    public AuthController(EmailAuthService emailAuthService) {
        this.emailAuthService = emailAuthService;
    }

    @Operation(summary = "이메일 전송", description = "가입 가능한 이메일 검증 후 이메일 전송")
    @PostMapping("/email-request")
    public String requestEmailAuth(@RequestBody String email) {
        emailAuthService.sendAuthCode(email);
        return "인증 번호가 발송되었습니다.";
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증 번호 확인")
    @PostMapping("/email-verify")
    public String verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        emailAuthService.verifyCode(request.email(), request.authCode());
        return "인증에 성공하였습니다.";
    }
}
