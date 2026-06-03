package com.example.nevera.controller;


import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.common.response.ApiResponse;
import com.example.nevera.dto.inventory.OcrRefineResponse;
import com.example.nevera.repository.OcrJobStore;
import com.example.nevera.service.LlmService;
import com.example.nevera.service.OcrService;
import com.example.nevera.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@Tag(name = "Ocr", description = "Ocr 관련 API")
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OcrController {

    private final OcrService ocrService;
    private final LlmService llmService;
    private final SseService sseService;
    private final OcrJobStore ocrJobStore;

    // 1. jobId 발급
    @Operation(summary = "OCR 작업 ID 발급", description = "OCR 작업 시작 전 jobId를 발급")
    @PostMapping("/jobs")
    public ApiResponse<Map<String, String>> createJob() {
        String jobId = ocrJobStore.createJob();
        return ApiResponse.success(Map.of("jobId", jobId));
    }

    // 2. SSE 연결
    @Operation(summary = "OCR 진행률 조회", description = "SSE 연결을 통해 OCR 진행률을 실시간으로 수신")
    @GetMapping(value = "/progress/{jobId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter progress(@PathVariable String jobId) {
        if (!ocrJobStore.exists(jobId)) {
            throw new BusinessException(ErrorCode.OCR_PROCESS_ERROR);
        }
        return sseService.createEmitter(jobId);
    }

    // 3. 이미지 업로드 + 작업 시작
    @Operation(summary = "재료 스캔", description = "영수증 재료스캔")
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<OcrRefineResponse>> extract(
            @RequestPart("file") MultipartFile file,
            @RequestParam String jobId) {

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        if (!ocrJobStore.exists(jobId)) {
            throw new BusinessException(ErrorCode.INVALID_JOB_ID);
        }

        // OCR 시작 0%
        sseService.send(jobId, 0, null);

        List<String> rawTexts = ocrService.extractTextFromImage(file);

        // OCR 완료 20%
        sseService.send(jobId, 20, null);

        List<OcrRefineResponse> refined = llmService.refineIngredientData(rawTexts);

        // LLM 완료 100%
        sseService.send(jobId, 100, null);

        ocrJobStore.remove(jobId);

        return ApiResponse.success(refined);
    }

}