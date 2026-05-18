package com.example.nevera.dto.wish;

import com.example.nevera.entity.WishEntity;

public record WishResponse(
        Long id,
        String name,
        Long amount
) {
    public static WishResponse from(WishEntity wish) {
        return new WishResponse(
                wish.getId(),
                wish.getName(),
                wish.getAmount()
        );
    }
}
