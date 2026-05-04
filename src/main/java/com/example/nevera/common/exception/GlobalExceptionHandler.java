package com.example.nevera.common.exception;

import com.example.nevera.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode(); // Enum에서 정보를 꺼냄

        return ResponseEntity
                .status(errorCode.getStatus()) //  409, 400 등 상태코드 설정
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage())); //  JSON 바디
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        String message = "요청 형식이 잘못되었습니다.";
        int code = 3012;
        Throwable cause = e.getCause();

        // 1. 데이터 타입 불일치 (예: Integer 필드에 String "e333" 입력)
        if (cause instanceof tools.jackson.databind.exc.InvalidFormatException target) {
            String fieldName = target.getPath().isEmpty() ? "알 수 없는 필드" : target.getPath().getFirst().getPropertyName();
            message = String.format("'%s' 필드의 타입이 올바르지 않습니다. (기대 타입: %s)",
                    fieldName, target.getTargetType().getSimpleName());
            code = 3010;
        }

        // 2. JSON 문법 오류 (예: 콤마 누락, 중괄호 미닫힘)
        else if (cause instanceof tools.jackson.core.exc.StreamReadException target) {
            message = "JSON 형식이 올바르지 않습니다.";
            code = 3011;
        }

        // 3. 아예 본문이 비어 있는 경우
        else if (e.getMessage() != null && e.getMessage().contains("Required request body is missing")) {
            message = "요청 본문(Body)이 비어있습니다.";
            code = 3000;
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(code, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        // 유효성 검사 에러 중 첫 번째 메시지를 가져옴 (@NotBlank의 message 속성값)
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(3001, errorMessage));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        String fieldName = e.getName();

        String message = String.format("'%s' 필드의 타입이 잘못되었습니다. (기대 타입: %s)",
                fieldName, Objects.requireNonNull(e.getRequiredType()).getSimpleName());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(3002, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("잘못된 요청입니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(3003, message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(3004, "지원하지 않는 HTTP 메서드입니다."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(3005, "요청하신 경로를 찾을 수 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(5000, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}