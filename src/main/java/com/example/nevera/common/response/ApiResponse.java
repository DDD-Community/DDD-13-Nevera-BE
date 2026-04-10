package com.example.nevera.common.response;
// 패키지 경로 확인!

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private T result;           // 성공 시 DTO 등이 들어감
    private ErrorResponse error; // 실패 시 에러 객체 (성공 시 null)

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(result, null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(null, new ErrorResponse(code, message));
    }

    @Getter
    @AllArgsConstructor
    static class SuccessBody {
        private String message;
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private int code;
        private String message;
    }
}