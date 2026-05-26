package com.example.nevera.controller;


import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.OcrRefineResponse;
import com.example.nevera.service.LlmService;
import com.example.nevera.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Ocr", description = "Ocr 관련 API")
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OcrController {

    private final OcrService ocrService;
    private final LlmService llmService;

    @Operation(summary = "재료 스캔", description = "영수증 재료스캔")
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<OcrRefineResponse>> extract(
            @RequestPart("file") MultipartFile file) {

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        List<String> rawTexts = ocrService.extractTextFromImage(file);
        List<OcrRefineResponse> refined = llmService.refineIngredientData(rawTexts);

        return ApiResponse.success(refined);
    }


}
