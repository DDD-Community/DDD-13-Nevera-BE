package com.example.nevera.dto.wish;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WishRequest(
        @NotBlank(message = "목표 이름은 필수 입력 값입니다.")
        String name,
        @NotNull(message = "목표 금액은 필수 입력 값입니다.")
        Integer amount
) {}
