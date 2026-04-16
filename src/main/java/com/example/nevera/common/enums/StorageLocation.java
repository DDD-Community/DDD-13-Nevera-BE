package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageLocation {
    FRIDGE("냉장"),
    FREEZER("냉동"),
    PANTRY("실온");

    private final String displayName;
}
