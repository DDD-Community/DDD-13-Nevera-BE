package com.example.nevera.controller;

import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.InventoryRequest;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory", description = "냉장고 재고 관련 API")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;
    private final MessageSource messageSource;

    @Operation(summary = "재료 등록", description = "여러 재료를 한 번에 등록 (최대 100개)")
    @PostMapping
    public ApiResponse<Boolean> create(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody @Size(max = 100, message = "{validation.inventory.requests.max_size}") List<InventoryRequest> requests
    ) {
        inventoryService.create(memberId, requests);
        return ApiResponse.success(true);
    }

    @Operation(summary = "재료 수정", description = "재료 단건 수정")
    @PutMapping("/{inventoryId}")
    public ApiResponse<InventoryResponse> update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryRequest request
    ) {
        return ApiResponse.success(inventoryService.update(memberId, inventoryId, request));
    }
}
