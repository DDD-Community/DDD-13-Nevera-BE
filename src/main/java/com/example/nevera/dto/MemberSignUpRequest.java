package com.example.nevera.dto;

import com.example.nevera.common.enums.MemberRole;
import com.example.nevera.entity.MemberEntity;

public record MemberSignUpRequest(
        String email,
        String password,
        String name,
        String provider,
        String status
) {

    public MemberEntity toEntity(String encodedPassword, MemberRole defaultRole) {
        return MemberEntity.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .provider(provider)
                .status(status)
                .role(defaultRole)
                .build();
    }
}