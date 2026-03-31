package com.example.nevera.dto.auth;

public record EmailVerifyRequest(
        String email,
        String authCode
) {


    @Override
    public String email() {
        return email;
    }

    @Override
    public String authCode() {
        return authCode;
    }
}
