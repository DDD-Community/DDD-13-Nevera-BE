package com.example.nevera.dto.wish;

import com.example.nevera.entity.WishEntity;

import java.time.LocalDate;

public record WishResponse(
        Long id,
        String name,
        int amount,
        LocalDate createdAt
) {
    public static WishResponse from(WishEntity wish) {
        return new WishResponse(
                wish.getId(),
                wish.getName(),
                wish.getAmount(),
                wish.getCreatedAt().toLocalDate()
        );
    }
}
