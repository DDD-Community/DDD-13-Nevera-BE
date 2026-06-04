package com.example.nevera.service;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientUnit;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class LlmService {

    private final ChatModel chatModel;

    private final ObjectMapper objectMapper;

    public List<OcrRefineResponse> refineIngredientData(List<String> rawTexts) {

        // enum을 한국어 힌트와 함께 넘기기
        String categoryList = Arrays.stream(Category.values())
                .map(c -> c.name())
                .collect(Collectors.joining(", "));

        String locationList = Arrays.stream(StorageLocation.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        String unitList = Arrays.stream(IngredientUnit.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        // 1. 오늘 날짜 구하기 (예: 2026-06-01)
        String today = java.time.LocalDate.now().toString();

// 2. 프롬프트 포맷팅
        String prompt = String.format("""
        오늘 날짜는 %s 입니다.
        영수증 또는 이커머스 주문내역 텍스트에서 식재료를 추출해 JSON 배열만 반환해. 설명 없이 JSON만.
        
        규칙:
        - name: 핵심 재료명 (브랜드 제거, 예: "CJ 두부" → "두부")
        - category: [%s] 중 택1, 애매하거나 분류 불가능한 경우 ETC 선택
        - location: [%s] 중 택1
        - quantity: 숫자만 (불명확하면 1)
        - cost: 최종 금액 숫자만 (할인가 적용된 금액), 금액을 읽을 수 없는 경우 0
        - expirationDate: 오늘 날짜를 기준으로, 해당 식재료의 일반적인 권장 유통기한(또는 보관 기한)을 더한 날짜 (YYYY-MM-DD 형식). 계산이 불가능하면 빈 문자열("")
        - 식재료가 아닌 항목(배송비, 쿠폰, 포인트 등)은 제외
        
        출력 예시:
        [{"name":"가지","category":"VEGETABLE","location":"FRIDGE","quantity":1,"cost":1500,"expirationDate":"2026-06-11"}]
        
        텍스트: %s
        """,
                today, categoryList, locationList,
                String.join(", ", rawTexts)
        );

        long start = System.currentTimeMillis();
        log.info("Gemini 호출 시작... 항목 수: {}", rawTexts.size());

        String response;
        try {
            response = chatModel.call(prompt);
        } catch (Exception e) {
            log.error("Gemini 호출 실패: ", e);
            throw new BusinessException(ErrorCode.LLM_GENERATE_ERROR);
        }

        log.info("Gemini 응답 수신 완료! 소요시간: {}ms", System.currentTimeMillis() - start);

        try {
            String jsonOutput = response.replaceAll("(?s)```json|```", "").trim();
            return objectMapper.readValue(jsonOutput, new TypeReference<List<OcrRefineResponse>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("파싱 실패. 응답 원문: {}", response);
            throw new BusinessException(ErrorCode.LLM_PARSE_ERROR);
        }
    }
}