package com.example.nevera.dto.wish;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WishRequest(
        @NotBlank(message = "{validation.wish.name.not_blank}")
        @Size(max = 15, message = "{validation.wish.name.size}")
        String name,
        @NotNull(message = "{validation.wish.amount.not_null}")
        @Max(value = 9999999999L, message = "{validation.wish.amount.max}")
        Long amount
) {}
