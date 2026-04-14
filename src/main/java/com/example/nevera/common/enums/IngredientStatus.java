package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IngredientStatus {
    ACTIVE("활성화"),
    CONSUMED("소비완료"),
    WASTED("폐기");

    private final String displayName;
}