package com.example.nevera.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_AUTH_CODE(400, 2001, "error.auth.invalid_auth_code"),
    EXPIRED_AUTH_CODE(400, 2002, "error.auth.expired_auth_code"),
    UNVERIFIED_EMAIL(400, 2003, "error.auth.unverified_email"),
    INVALID_PASSWORD(400, 2004, "error.auth.invalid_password"),
    AUTH_NOT_FOUND(404, 2005, "error.auth.auth_not_found"),
    DUPLICATE_EMAIL(409, 2006, "error.auth.duplicate_email"),
    MAIL_SEND_ERROR(500, 2007, "error.auth.mail_send_error"),
    LOGIN_FAILED(401, 2008, "error.auth.login_failed"),


    INVALID_GOOGLE_TOKEN(401, 2011, "error.auth.invalid_google_token"),

    INVALID_TOKEN(401, 2021, "error.auth.invalid_token"),
    EXPIRED_TOKEN(401, 2022, "error.auth.expired_token"),
    TOKEN_NOT_FOUND(404, 2023, "error.auth.token_not_found"),

    MEMBER_NOT_FOUND(404, 2041, "error.member.member_not_found"),

    OCR_PROCESS_ERROR(500, 3001, "error.ocr.process_error"),
    INVALID_IMAGE_FORMAT(400, 3002, "error.ocr.invalid_image_format"),
    GOOGLE_VISION_API_ERROR(500, 3003, "error.ocr.google_vision_api_error"),
    EMPTY_IMAGE_FILE(400, 3004, "error.ocr.empty_image_file"),

    LLM_GENERATE_ERROR(500, 5002, "error.llm.generate_error"),
    LLM_PARSE_ERROR(500, 5001, "error.llm.parse_error"),

    FCM_TOKEN_NOT_FOUND(404, 2051, "error.fcm.fcm_token_not_found"),
    FCM_TOKEN_INVALID(400, 2052, "error.fcm.fcm_token_invalid"),
    FCM_SEND_ERROR(500, 2053, "error.fcm.fcm_send_error"),

    INVENTORY_NOT_FOUND(404, 4001, "error.inventory.inventory_not_found"),
    INVENTORY_FORBIDDEN(403, 4002, "error.inventory.inventory_forbidden"),

    WISH_NOT_FOUND(404, 4051, "error.wish.wish_not_found"),
    WISH_FORBIDDEN(403, 4052, "error.wish.wish_forbidden");



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
