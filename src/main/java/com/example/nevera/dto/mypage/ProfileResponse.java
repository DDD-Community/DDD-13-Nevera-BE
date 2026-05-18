package com.example.nevera.dto.mypage;

import com.example.nevera.entity.Member;

public record ProfileResponse(
        String profileImageUrl,
        String nickname,
        String email,
        boolean hasWish
) {
    public static ProfileResponse from(Member member, boolean hasWish) {
        return new ProfileResponse(
                member.getProfileImageUrl(),
                member.getNickname(),
                member.getEmail(),
                hasWish
        );
    }
}