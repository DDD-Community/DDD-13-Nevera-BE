package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.InventoryRequest;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory", description = "냉장고 재고 관련 API")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "재료 등록", description = "새로운 재료 등록")
    @PostMapping
    public ApiResponse<InventoryResponse> create(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody InventoryRequest request
    ) {
        return ApiResponse.success(inventoryService.create(memberId, request));
    }

    @Operation(summary = "재료 전체 조회", description = "로그인한 사용자의 전체 Active 재료 조회")
    @GetMapping
    public ApiResponse<List<InventoryResponse>> getAllActive(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(inventoryService.getAllActive(memberId));
    }

    @Operation(summary = "소비 완료 재료 조회", description = "로그인한 사용자의 소비 완료 재료 조회")
    @GetMapping("/consumed")
    public ApiResponse<List<InventoryResponse>> getAllConsumed(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(inventoryService.getAllConsumed(memberId));
    }

    @Operation(summary = "폐기 재료 조회", description = "로그인한 사용자의 폐기 재료 조회")
    @GetMapping("/wasted")
    public ApiResponse<List<InventoryResponse>> getAllWasted(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(inventoryService.getAllWasted(memberId));
    }

    @Operation(summary = "재료 하나 조회", description = "특정 재료 조회")
    @GetMapping("/{inventoryId}")
    public ApiResponse<InventoryResponse> getOne(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inventoryId
    ) {
        return ApiResponse.success(inventoryService.getOne(memberId, inventoryId));
    }

    @Operation(summary = "재료 수정", description = "특정 재료 수정")
    @PutMapping("/{inventoryId}")
    public ApiResponse<InventoryResponse> update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryRequest request
    ) {
        return ApiResponse.success(inventoryService.update(memberId, inventoryId, request));
    }

    @Operation(summary = "재료 삭제", description = "특정 재료 삭제")
    @DeleteMapping("/{inventoryId}")
    public ApiResponse<?> delete(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inventoryId
    ) {
        inventoryService.delete(memberId, inventoryId);
        return ApiResponse.success(new ApiResponse.SuccessBody("재료 삭제 성공"));
    }
}
