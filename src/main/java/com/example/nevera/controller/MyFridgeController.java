package com.example.nevera.controller;


import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.SortType;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.FridgeInventoryResponse;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.service.MyFridgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name="Fridge", description="나의 냉장고 API")
@RestController
@RequestMapping("/api/v1/myfridge")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
public class MyFridgeController {

    private final MyFridgeService myFridgeService;
    @Operation(summary = "식재료 목록 조회", description = "보관방법, 카테고리, 정렬 조건에 따른 식재료 목록을 무한 스크롤로 조회합니다.")
    @GetMapping
    public ApiResponse<Slice<FridgeInventoryResponse>> getIngredients(
            @RequestParam(required = false) StorageLocation storageLocation,
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "EXPIRY_DATE ") SortType sortType,
            Pageable pageable) {
        Sort sort = (sortType == SortType.LATEST)
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "expirationDate");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Slice<FridgeInventoryResponse> response = myFridgeService.getIngredients(storageLocation, category, sortedPageable);
        return ApiResponse.success(response);
    }

//    @Operation(summary = "식재료 수정", description = "등록된 식재료의 상세 정보를 수정합니다.")
//    @PutMapping("/{id}")
//    public ApiResponse<InventoryResponse> updateIngredient(
//            @PathVariable Long id,
//            @RequestBody @Valid IngredientUpdateRequestDto requestDto) {
//
//        IngredientResponseDto response = ingredientService.updateIngredient(id, requestDto);
//        return ApiResponse.success(response);
//    }
//
//    @Operation(summary = "식재료 구조 처리", description = "사용(구조)한 비율(25, 50, 75, 100)만큼 잔여 금액/수량을 차감합니다. 100% 선택 시 목록에서 삭제됩니다.")
//    @PatchMapping("/{id}/rescue")
//    public ApiResponse<Void> rescueIngredient(
//            @PathVariable Long id,
//            @RequestBody @Valid IngredientStatusChangeRequestDto requestDto) {
//
//        ingredientService.processRescue(id, requestDto.getPercentage());
//        return ApiResponse.success(null);
//    }
//
//    @Operation(summary = "식재료 폐기 처리", description = "버림(폐기) 처리한 비율(25, 50, 75, 100)만큼 잔여 금액/수량을 차감합니다. 100% 선택 시 목록에서 삭제됩니다.")
//    @PatchMapping("/{id}/discard")
//    public ApiResponse<Void> discardIngredient(
//            @PathVariable Long id,
//            @RequestBody @Valid IngredientStatusChangeRequestDto requestDto) {
//
//        ingredientService.processDiscard(id, requestDto.getPercentage());
//        return ApiResponse.success(null);
//    }

}