package com.example.nevera.common.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {



        // 1. Swagger 관련 클래스나 패키지인지 확인하여 제외
        if (returnType.getDeclaringClass().getName().contains("springdoc") ||
                returnType.getDeclaringClass().getName().contains("swagger")) {
            return false;
        }

        // 2. 만약 컨트롤러 패키지를 명확히 분리했다면, 내 컨트롤러일 때만 true 반환
        // return returnType.getDeclaringClass().getName().startsWith("com.example.nevera.controller");

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 이미 ApiResponse 형태이거나 에러 응답인 경우 그대로 반환
        if (body instanceof ApiResponse) {
            return body;
        }

        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(ApiResponse.success(body));
            } catch (Exception e) {
                throw new RuntimeException("JSON 변환 에러");
            }
        }

        // 그 외 모든 결과(DTO, List 등)를 ApiResponse.success()로 감싸기
        return ApiResponse.success(body);
    }
}