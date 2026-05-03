package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.ConsumedWastedResponse;
import com.example.nevera.dto.savings.MainSummaryResponse;
import com.example.nevera.dto.wish.WishRequest;
import com.example.nevera.dto.wish.WishResponse;
import com.example.nevera.service.HomeService;
import com.example.nevera.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Savings", description = "절약/폐기 금액 조회 API")
@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class HomeController {

    private final HomeService savingsService;
    private final WishService wishService;

    @Operation(summary = "이번 주 메인 요약 조회", description = "이번 주 절감액, 현재 목표 금액 조회 (절감액 = 소비 금액 - 폐기 금액)")
    @GetMapping("/summary/week")
    public ApiResponse<MainSummaryResponse> getWeeklySummary(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(savingsService.getWeeklySummary(memberId));
    }

    @Operation(summary = "이번 달 메인 요약 조회", description = "이번 달 절감액, 현재 목표 금액 조회 (절감액 = 소비 금액 - 폐기 금액)")
    @GetMapping("/summary/month")
    public ApiResponse<MainSummaryResponse> getMonthlySummary(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(savingsService.getMonthlySummary(memberId));
    }

    @Operation(summary = "소비 완료 목록 조회 (무한스크롤)", description = "offset, limit으로 페이징. 기본값: offset=0, limit=20")
    @GetMapping("/consumed")
    public ApiResponse<List<ConsumedWastedResponse>> getConsumed(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ApiResponse.success(savingsService.getConsumed(memberId, offset, limit));
    }

    @Operation(summary = "폐기 목록 조회 (무한스크롤)", description = "offset, limit으로 페이징. 기본값: offset=0, limit=20")
    @GetMapping("/wasted")
    public ApiResponse<List<ConsumedWastedResponse>> getWasted(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ApiResponse.success(savingsService.getWasted(memberId, offset, limit));
    }

    @Operation(summary = "목표 금액 등록", description = "새로운 목표를 등록하면 기존 목표는 삭제됩니다.")
    @PostMapping("/wish")
    public ApiResponse<WishResponse> registerWish(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody WishRequest request
    ) {
        return ApiResponse.success(wishService.register(memberId, request));
    }

    @Operation(summary = "목표 금액 수정")
    @PutMapping("/wish/{wishId}")
    public ApiResponse<WishResponse> updateWish(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long wishId,
            @Valid @RequestBody WishRequest request
    ) {
        return ApiResponse.success(wishService.update(memberId, wishId, request));
    }

    @Operation(summary = "목표 금액 삭제")
    @DeleteMapping("/wish/{wishId}")
    public ApiResponse<?> deleteWish(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long wishId
    ) {
        wishService.delete(memberId, wishId);
        return ApiResponse.success(new ApiResponse.SuccessBody("목표가 삭제되었습니다."));
    }
}
