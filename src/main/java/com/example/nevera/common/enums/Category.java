package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    VEG("채소"),
    FRUIT("과일"),
    MEATEGGS("육류/계란"),
    SEA("해산물"),
    DAIRY("유제품"),
    SAUCE("소스/양념"),
    DRINK("음료"),
    PROCESSED("가공식품"),
    ETC("기타");

    private final String displayName;
}
