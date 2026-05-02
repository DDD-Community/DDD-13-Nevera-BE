package com.example.nevera.dto.wish;

import com.example.nevera.entity.WishEntity;

import java.time.OffsetDateTime;

public record WishResponse(
        String name,
        int amount
) {
    public static WishResponse from(WishEntity wish) {
        return new WishResponse(
                wish.getName(),
                wish.getAmount()
        );
    }
}
