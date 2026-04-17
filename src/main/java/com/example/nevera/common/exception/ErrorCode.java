package com.example.nevera.common.exception;

public enum ErrorCode {

    INVALID_AUTH_CODE(400, 2001, "인증 번호가 일치하지 않습니다."),
    EXPIRED_AUTH_CODE(400, 2002, "인증 시간이 만료되었습니다."),
    UNVERIFIED_EMAIL(400, 2003, "이메일 인증이 완료되지 않았습니다."),
    INVALID_PASSWORD(400, 2004, "비밀번호가 일치하지 않습니다."),
    AUTH_NOT_FOUND(404, 2005, "인증 요청 내역이 없습니다."),
    DUPLICATE_EMAIL(409, 2006, "이미 사용 중인 이메일입니다."),
    MAIL_SEND_ERROR(500, 2007, "메일 발송 중 오류가 발생했습니다."),

    INVALID_GOOGLE_TOKEN(401, 2011, "유효하지 않은 Google 토큰입니다."),

    INVALID_TOKEN(401, 2021, "유효하지 않거나 권한이 없거나 토큰이 없습니다."),
    EXPIRED_TOKEN(401, 2022, "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(404, 2023, "토큰을 찾을 수 없습니다."),

    MEMBER_NOT_FOUND(404, 2041, "존재하지 않는 사용자입니다."),

    OCR_PROCESS_ERROR(500, 3001, "이미지 분석 중 오류가 발생했습니다."),
    INVALID_IMAGE_FORMAT(400, 3002, "지원하지 않는 이미지 형식입니다."),
    GOOGLE_VISION_API_ERROR(500, 3003, "구글 비전 API 호출에 실패했습니다."),
    EMPTY_IMAGE_FILE(400, 3004, "업로드된 이미지 파일이 비어있습니다."),

    INVENTORY_NOT_FOUND(404, 4001, "존재하지 않는 재고입니다."),
    INVENTORY_FORBIDDEN(403, 4002, "해당 재고에 대한 권한이 없습니다.");



    private final int status;
    private final int code;
    private final String message;

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ErrorCode(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
