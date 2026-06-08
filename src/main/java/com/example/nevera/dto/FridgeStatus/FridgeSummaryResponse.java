package com.example.nevera.dto.FridgeStatus;

public record FridgeSummaryResponse(
        String statusMessage,
        ExpirySummary expiry,
        LocationSummary byLocation
) {
    public record ExpirySummary(
            long pantry,
            long imminent,
            long expired,
            long total,
            int imminentDays
    ) {}

    public record LocationSummary(
            long fridge,
            long freezer,
            long roomTemp
    ) {}
}
