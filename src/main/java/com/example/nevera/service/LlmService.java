package com.example.nevera.service;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.inventory.OcrRefineRequest;
import com.example.nevera.dto.inventory.OcrRefineResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.ai.chat.model.ChatModel;


import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class LlmService {

    private final ChatModel chatModel;

    private final ObjectMapper objectMapper;

    public List<OcrRefineResponse> refineIngredientData(List<String> rawTexts) {
        // 1. 프롬프트 구성
        String systemInstruction = """
            너는 영수증 전문가야. 텍스트 리스트에서 식재료를 추출해 JSON 배열로 응답해.
            규칙:
            1. name: 핵심 재료명만 (예: 'CJ 두부' -> '두부')
            2. category: [%s] 중 하나 선택
            3. location: [%s] 중 하나 선택
            4. quantity: 숫자만 (모르면 1)
            5. unit: [%s] 중 하나 선택
            6. cost: 최종 금액 (숫자만)
            """;

        String prompt = String.format(systemInstruction,
                Arrays.toString(Category.values()),
                Arrays.toString(StorageLocation.values()),
                Arrays.toString(IngredientUnit.values())
        ) + "\n영수증 텍스트: " + String.join(", ", rawTexts);

        // 2. Gemini 호출
        String response;
        try {


            log.info("Gemini 호출 시작... 보낼 텍스트 길이: {}", rawTexts.size());
            response = chatModel.call(prompt);
            System.out.println(prompt);
            log.info("Gemini 응답 수신 성공!");
        } catch (Exception e) {

            log.error("Gemini 호출 실패! 상세 원인: ", e);
            throw new BusinessException(ErrorCode.LLM_GENERATE_ERROR); // 기존 에러 코드 사용
        }

        // 3. JSON 파싱
        try {

            String jsonOutput = response.replaceAll("```json|```", "").trim();
            return objectMapper.readValue(jsonOutput, new TypeReference<List<OcrRefineResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.LLM_PARSE_ERROR);
        }
    }
}