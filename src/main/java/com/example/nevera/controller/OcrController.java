package com.example.nevera.controller;


import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.OcrRefineResponse;
import com.example.nevera.service.LlmService;
import com.example.nevera.service.OcrService;
import com.example.nevera.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "Ocr", description = "Ocr 관련 API")
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OcrController {

    private final OcrService ocrService;
    private final LlmService llmService;
    private final SseService sseService;

    @Operation(summary = "재료 스캔", description = "영수증 재료스캔")
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<OcrRefineResponse>> extract(
            @RequestPart("file") MultipartFile file,
            @RequestParam String jobId) {

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
        sseService.send(jobId, 0);
        List<String> rawTexts = ocrService.extractTextFromImage(file);

        sseService.send(jobId, 20);
        List<OcrRefineResponse> refined = llmService.refineIngredientData(rawTexts);
        sseService.send(jobId, 100);
        return ApiResponse.success(refined);
    }

    @Operation(summary = "SSE", description = "Server-Sent Events")
    @GetMapping(value = "/progress/{jobId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter progress(@PathVariable String jobId) {
        return sseService.createEmitter(jobId);
    }


}
