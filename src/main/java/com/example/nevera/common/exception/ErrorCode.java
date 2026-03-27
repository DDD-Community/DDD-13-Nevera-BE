package com.example.nevera.common.exception;

public enum ErrorCode {

    INVALID_AUTH_CODE(400, "인증 번호가 일치하지 않습니다."),
    EXPIRED_AUTH_CODE(400, "인증 시간이 만료되었습니다."),
    UNVERIFIED_EMAIL(400, "이메일 인증이 완료되지 않았습니다."),

    AUTH_NOT_FOUND(404, "인증 요청 내역이 없습니다."),

    DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다."),


    MAIL_SEND_ERROR(500, "메일 발송 중 오류가 발생했습니다.");

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
