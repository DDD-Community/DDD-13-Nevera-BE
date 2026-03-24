package com.example.nevera.common.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 모든 컨트롤러 응답에 적용 (단, String 응답 등 예외가 필요하면 여기서 처리)
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

        // 그 외 모든 결과(DTO, List 등)를 ApiResponse.success()로 감싸기
        return ApiResponse.success(body);
    }
}