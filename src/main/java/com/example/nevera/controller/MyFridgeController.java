package com.example.nevera.controller;


import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.SortType;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.FridgeInventoryResponse;
import com.example.nevera.dto.inventory.InventoryProcessRequest;
import com.example.nevera.dto.inventory.InventoryProcessResponse;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.dto.inventory.InventoryUpdateRequest;
import com.example.nevera.service.MyFridgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name="Fridge", description="나의 냉장고 API")
@RestController
@RequestMapping("/api/v1/fridge")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
public class MyFridgeController {

    private final MyFridgeService myFridgeService;
    @Operation(summary = "식재료 목록 조회", description = "보관방법, 카테고리, 정렬 조건에 따른 식재료 목록을 무한 스크롤로 조회합니다.")
    @GetMapping("/ingredients")
    public ApiResponse<Slice<FridgeInventoryResponse>> getIngredients(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) StorageLocation storageLocation,
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "EXPIRY_DATE") SortType sortType,
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        Sort sort = (sortType == SortType.LATEST)
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "expirationDate");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Slice<FridgeInventoryResponse> response = myFridgeService.getIngredients(
                memberId, storageLocation, category, sortedPageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "식재료 상세 조회", description = "수정 화면 진입 시 식재료 ID를 기반으로 최신 상세 정보를 조회합니다.")
    @GetMapping("/{inventoryId}")
    public ApiResponse<InventoryResponse> getIngredientDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inventoryId) {


        InventoryResponse response = myFridgeService.getIngredientDetail(memberId, inventoryId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "식재료 수정", description = "식재료의 일반 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<InventoryResponse> updateIngredient(
                                                           @AuthenticationPrincipal Long memberId,
                                                           @PathVariable Long id,
                                                           @RequestBody @Valid InventoryUpdateRequest requestDto) {

        return ApiResponse.success(myFridgeService.updateIngredient(memberId, id, requestDto));
    }

    @Operation(summary = "식재료 구조/폐기", description = "최초 등록 금액 기준으로 구조 또는 폐기합니다.")
    @PatchMapping("/{inventoryId}/process")
    public ApiResponse<InventoryProcessResponse> processIngredient(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inventoryId,
            @RequestBody @Valid InventoryProcessRequest request) {
        return ApiResponse.success(
                myFridgeService.processIngredient(memberId, inventoryId, request));
    }

}
