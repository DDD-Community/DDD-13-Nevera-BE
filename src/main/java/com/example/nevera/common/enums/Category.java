package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    VEGEFRUIT("채소, 버섯, 과일"),
    FAMINE("구황작물"),
    MEAT("육류"),
    SEA("생선 및 해산물"),
    EGG("계란"),
    TOFU("두부"),
    CANDRY("통조림, 건식품"),
    FRZCONV("냉동식품, 간편식"),
    GRAINS("곡류"),
    MSG("조미료");

    private final String displayName;
}
