package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WishStatus {
    ACTIVE("도전 중"),
    ACHIEVED("달성완료"),
    FAILED("실패");

    private final String displayName;
}