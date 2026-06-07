package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.mypage.*;
import com.example.nevera.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Mypage", description = "온보딩, 마이페이지 관련 API")
@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MypageController {
    private final MemberService memberService;

    @Value("${terms.service-url}")
    private String termsOfServiceUrl;

    @Value("${terms.privacy-url}")
    private String privacyPolicyUrl;

    @Operation(summary = "약관 조회", description = "이용약관·개인정보처리방침 노션 링크 반환")
    @GetMapping("/terms")
    public ApiResponse<TermsResponse> getTerms() {
        return ApiResponse.success(new TermsResponse(termsOfServiceUrl, privacyPolicyUrl));
    }

    @Operation(summary = "내 프로필 조회", description = "프로필 이미지·닉네임·이메일·목표 등록 여부 반환")
    @GetMapping("/me/profile")
    public ApiResponse<ProfileResponse> getProfile(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(memberService.getProfile(memberId));
    }

    @Operation(summary = "알림 설정 조회", description = "로그인한 사용자의 유통기한 임박 알림 설정 조회")
    @GetMapping("/notification")
    public ApiResponse<NotificationSettingResponse> getNotificationSetting(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(memberService.getNotificationSetting(memberId));
    }

    @Operation(summary = "닉네임 수정", description = "사용자 닉네임 수정")
    @PutMapping("/nickname")
    public ApiResponse<ProfileResponse> updateNickname(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody NicknameRequest request
    ) {
        return ApiResponse.success(memberService.updateNickname(memberId, request));
    }

    @Operation(summary = "알림 수신 여부 수정", description = "유통기한 임박 알림 수신 여부(on/off) 수정")
    @PutMapping("/notification/enabled")
    public ApiResponse<NotificationSettingResponse> updateNotificationEnabled(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody NotificationEnabledRequest request
    ) {
        return ApiResponse.success(memberService.updateNotificationEnabled(memberId, request));
    }

    @Operation(summary = "알림 시간 수정", description = "유통기한 임박 알림 수신 시간(시·분) 수정")
    @PutMapping("/notification/time")
    public ApiResponse<NotificationSettingResponse> updateNotificationTime(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody NotificationTimeRequest request
    ) {
        return ApiResponse.success(memberService.updateNotificationTime(memberId, request));
    }

    @Operation(summary = "닉네임, 위시 등록 여부 조회", description = "온보딩 완료 여부 확인을 위한 닉네임, 위시 등록 여부 조회")
    @GetMapping("/onboarding/complete")
    public ApiResponse<OnboardingCompleteResponse> getOnboardingCompleted(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(memberService.getOnboardingCompletedStatus(memberId));
    }
}
