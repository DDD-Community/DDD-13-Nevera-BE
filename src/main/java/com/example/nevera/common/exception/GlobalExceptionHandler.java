package com.example.nevera.common.exception;

import com.example.nevera.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode(); // Enum에서 정보를 꺼냄

        return ResponseEntity
                .status(errorCode.getStatus()) //  409, 400 등 상태코드 설정
                .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage())); //  JSON 바디
    }
}