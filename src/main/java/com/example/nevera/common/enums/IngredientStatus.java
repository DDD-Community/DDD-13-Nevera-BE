package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IngredientStatus {
    ACTIVE("보관중"),
    CONSUMED("구조"),
    WASTED("폐기");

    private final String displayName;
}