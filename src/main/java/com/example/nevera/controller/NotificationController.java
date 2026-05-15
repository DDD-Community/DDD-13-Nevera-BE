package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.notification.FcmSendRequest;
import com.example.nevera.dto.notification.FcmTokenRequest;
import com.example.nevera.dto.notification.NotificationResponse;
import com.example.nevera.service.FcmService;
import com.example.nevera.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Tag(name = "Notification", description = "푸시 알림 관련 API")
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final FcmService fcmService;
    private final NotificationService notificationService;
    private final MessageSource messageSource;

    @Operation(summary = "FCM 토큰 등록", description = "로그인 후 디바이스의 FCM 토큰 저장")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/token")
    public ApiResponse<?> registerToken(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody FcmTokenRequest request
    ) {
        fcmService.saveToken(memberId, request.token());
        return ApiResponse.success(new ApiResponse.SuccessBody(
                messageSource.getMessage("success.notification.token_registered", null, Locale.KOREAN)));
    }

    @Operation(summary = "푸시 알림 전송 (테스트용)", description = "로그인한 본인에게 푸시 알림 전송")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/send")
    public ApiResponse<?> send(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody FcmSendRequest request
    ) {
        fcmService.sendNotification(memberId, request.title(), request.body());
        return ApiResponse.success(new ApiResponse.SuccessBody(
                messageSource.getMessage("success.notification.sent", null, Locale.KOREAN)));
    }

    @Operation(summary = "알림 목록 조회", description = "로그인한 사용자의 알림 목록을 최신순으로 조회 (무한스크롤, offset=0부터 시작)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/list")
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return ApiResponse.success(notificationService.getNotifications(memberId, offset));
    }
}
