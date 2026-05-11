package com.example.nevera.dto.savings;

import com.example.nevera.dto.wish.WishResponse;

public record MainSummaryResponse(
        int netSavings,
        int changePercent,
        WishResponse wish
) {}
