package com.example.nevera.dto.mypage;

import com.example.nevera.entity.Member;

public record ProfileResponse(
        String profileImageUrl,
        String nickname,
        String email,
        boolean hasWish
) {
    public static ProfileResponse from(Member member, boolean hasWish, String baseUrl) {
        return new ProfileResponse(
                toAbsoluteUrl(baseUrl, member.getProfileImageUrl()),
                member.getNickname(),
                member.getEmail(),
                hasWish
        );
    }

    private static String toAbsoluteUrl(String baseUrl, String imageUrl) {
        if (imageUrl == null || imageUrl.startsWith("http")) {
            return imageUrl; // 이미 절대 URL이거나 null이면 그대로
        }
        return baseUrl + imageUrl;
    }
}