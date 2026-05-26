package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.home.HomeSummaryResponse;
import com.example.nevera.dto.inventory.ConsumedWastedResponse;
import com.example.nevera.dto.wish.WishRequest;
import com.example.nevera.dto.wish.WishResponse;
import com.example.nevera.service.HomeService;
import com.example.nevera.service.WishService;
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

@Tag(name = "Savings", description = "절약/폐기 금액 조회 API")
@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class HomeController {

    private final HomeService savingsService;
    private final WishService wishService;
    private final MessageSource messageSource;

    @Operation(summary = "홈 화면 요약 조회", description = "닉네임, 위시 정보(누적 금액, 남은 금액, 달성 여부), 전체 구조/폐기 금액 조회")
    @GetMapping("/home")
    public ApiResponse<HomeSummaryResponse> getHomeSummary(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(savingsService.getHomeSummary(memberId));
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

    @Operation(summary = "목표 등록", description = "새로운 목표를 등록하면 기존 목표는 삭제됩니다.")
    @PostMapping("/wish")
    public ApiResponse<WishResponse> registerWish(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody WishRequest request
    ) {
        return ApiResponse.success(wishService.register(memberId, request));
    }

    @Operation(summary = "목표 수정", description = "달성된 목표는 수정할 수 없습니다.")
    @PutMapping("/wish/{wishId}")
    public ApiResponse<WishResponse> updateWish(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long wishId,
            @Valid @RequestBody WishRequest request
    ) {
        return ApiResponse.success(wishService.update(memberId, wishId, request));
    }

    @Operation(summary = "목표 삭제")
    @DeleteMapping("/wish/{wishId}")
    public ApiResponse<?> deleteWish(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long wishId
    ) {
        wishService.delete(memberId, wishId);
        return ApiResponse.success(new ApiResponse.SuccessBody(
                messageSource.getMessage("success.wish.deleted", null, Locale.KOREAN)));
    }
}
