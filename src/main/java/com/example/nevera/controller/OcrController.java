package com.example.nevera.controller;


import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.OcrRefineResponse;
import com.example.nevera.service.LlmService;
import com.example.nevera.service.OcrService;
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

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<OcrRefineResponse>>> extract(
            @RequestPart("file") MultipartFile file) {

        // 1. OCR로 텍스트 긁어오기
        List<String> rawTexts = ocrService.extractTextFromImage(file);

        // 2. Gemini로 데이터 정제하기
        List<OcrRefineResponse> refined = llmService.refineIngredientData(rawTexts);

        return ResponseEntity.ok(ApiResponse.success(refined));
    }


}
