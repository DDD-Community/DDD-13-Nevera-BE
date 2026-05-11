package com.example.nevera.dto.wish;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WishRequest(
        @NotBlank(message = "{validation.wish.name.not_blank}")
        String name,
        @NotNull(message = "{validation.wish.amount.not_null}")
        Integer amount
) {}
