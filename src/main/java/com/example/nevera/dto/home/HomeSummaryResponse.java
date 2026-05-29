package com.example.nevera.dto.home;

public record HomeSummaryResponse(
        String nickname,
        Long wishId,
        String wishName,
        Long wishAmount,
        Long accumulated,
        Long remaining,
        Boolean achieved,
        Long totalConsumed,
        Long totalWasted
) {}