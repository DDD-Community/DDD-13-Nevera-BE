package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.mypage.NotificationSettingRequest;
import com.example.nevera.dto.mypage.NotificationSettingResponse;
import com.example.nevera.dto.mypage.NotificationTimeRequest;
import com.example.nevera.dto.mypage.ProfileResponse;
import com.example.nevera.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Mypage", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MypageController {
    private final MemberService memberService;

    @Operation(summary = "내 프로필 조회", description = "프로필 이미지·닉네임·이메일·알림 수신 여부 반환")
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

    @Operation(summary = "알림 수신 여부 수정", description = "유통기한 임박 알림 수신 여부 수정")
    @PutMapping("/notification/enabled")
    public ApiResponse<NotificationSettingResponse> updateNotificationEnabled(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody NotificationSettingRequest request
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
}
