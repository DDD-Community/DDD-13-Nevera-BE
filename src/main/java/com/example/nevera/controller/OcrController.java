package com.example.nevera.controller;


import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.service.OcrService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Ocr", description = "Ocr 관련 API")
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/extract")
    public ResponseEntity<ApiResponse<List<String>>> extractText(@RequestParam("File") MultipartFile file) {

        // 1. 서비스 호출하여 텍스트 추출
        List<String> result = ocrService.extractTextFromImage(file);

        // 2. 결과 반환 (성공 시 ApiResponse.success 사용)
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
