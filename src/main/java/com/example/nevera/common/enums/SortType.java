package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {

    EXPIRY_DATE("유통기한순"),
    LATEST("최신순");

    private final String label;
}
