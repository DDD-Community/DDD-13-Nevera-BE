package com.example.nevera.common.exception;

public enum ErrorCode {

    DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다.");

    private final int status;
    private final String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
