package com.example.nevera.controller;

import com.example.nevera.dto.EmailVerifyRequest;
import com.example.nevera.service.EmailAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final EmailAuthService emailAuthService;

    public AuthController(EmailAuthService emailAuthService) {
        this.emailAuthService = emailAuthService;
    }

    @PostMapping("/email-request")
    public String requestEmailAuth(@RequestBody String email) {
        emailAuthService.sendAuthCode(email);
        return "인증 번호가 발송되었습니다.";
    }


    @PostMapping("/email-verify")
    public String verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        emailAuthService.verifyCode(request.email(), request.authCode());
        return "인증에 성공하였습니다.";
    }
}
