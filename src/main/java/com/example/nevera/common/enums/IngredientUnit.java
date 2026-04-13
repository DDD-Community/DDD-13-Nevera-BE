package com.example.nevera.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IngredientUnit {
    NONE("단위 없음"),
    G("그램"),
    KG("킬로그램"),
    ML("밀리리터"),
    L("리터"),
    EA("개"),
    PACK("팩"),
    CAN("캔"),
    BOTTLE("병");

    private final String displayName;
}
