package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.FridgeStatus.FridgeSummaryResponse;
import com.example.nevera.service.FridgeStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Status", description = "냉장고 현황 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FridgeStatusController {

    private final FridgeStatusService fridgeStatusService;

    @Operation(summary = "냉장고 현황 요약 조회", description = "유통기한 상태별 집계, 위치별 개수, 상태 메시지를 반환합니다.")
    @GetMapping("/summary")
    public ApiResponse<FridgeSummaryResponse> getSummary(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(fridgeStatusService.getSummary(memberId));
    }
}
