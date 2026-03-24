package com.example.nevera.common.exception;

public enum ErrorCode {

    DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(404, "해당 ID를 가진 유저를 찾을 수 없습니다.");

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
