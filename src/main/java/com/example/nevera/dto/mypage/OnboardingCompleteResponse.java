package com.example.nevera.dto.mypage;

public record OnboardingCompleteResponse(
        Boolean changedNickname,
        Boolean hasWish
) {
    public static OnboardingCompleteResponse from(Boolean changedNickname, Boolean hasWish) {
        return new OnboardingCompleteResponse(changedNickname, hasWish);
    }
}
