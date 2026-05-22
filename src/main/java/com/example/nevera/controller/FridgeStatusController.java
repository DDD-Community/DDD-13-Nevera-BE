package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.FridgeStatus.FridgeSummaryResponse;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.service.FridgeStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Status", description = "냉장고 현황 API")
@RestController
@RequestMapping("/api/v1/fridgestatus")
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

    @Operation(summary = "냉장고 재료 목록 조회", description = "ACTIVE 상태인 재료 목록을 카테고리 필터와 정렬 기준으로 조회합니다.")
    @GetMapping("/items")
    public ApiResponse<List<InventoryResponse>> getItems(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "EXPIRATION_ASC") String sort
    ) {
        return ApiResponse.success(fridgeStatusService.getItems(memberId, category));
    }
}
