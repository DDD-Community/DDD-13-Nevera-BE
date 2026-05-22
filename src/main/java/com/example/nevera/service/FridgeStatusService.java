package com.example.nevera.service;


import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.dto.FridgeStatus.ExpiryCountDto;
import com.example.nevera.dto.FridgeStatus.FridgeSummaryResponse;
import com.example.nevera.dto.FridgeStatus.FridgeSummaryResponse.ExpirySummary;
import com.example.nevera.dto.FridgeStatus.FridgeSummaryResponse.LocationSummary;
import com.example.nevera.dto.FridgeStatus.LocationCountDto;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.entity.Inventory;
import com.example.nevera.repository.FridgeStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FridgeStatusService {

    private static final int IMMINENT_DAYS = 1;

    private final FridgeStatusRepository fridgeStatusRepository;

    public FridgeSummaryResponse getSummary(Long memberId) {
        List<Inventory> inventories = fridgeStatusRepository.findAllByMemberId(memberId);

        ExpirySummary expirySummary = buildExpirySummary(inventories);
        LocationSummary locationSummary = buildLocationSummary(inventories);
        String statusMessage = resolveStatusMessage(expirySummary);

        return new FridgeSummaryResponse(statusMessage, expirySummary, locationSummary);
    }

    private ExpirySummary buildExpirySummary(List<Inventory> inventories) {
        OffsetDateTime now = OffsetDateTime.now();

        long expired = inventories.stream()
                .filter(i -> i.getExpirationDate().isBefore(now))
                .count();
        long imminent = inventories.stream()
                .filter(i -> !i.getExpirationDate().isBefore(now)
                        && i.getExpirationDate().isBefore(now.plusDays(IMMINENT_DAYS)))
                .count();
        long plenty = inventories.stream()
                .filter(i -> !i.getExpirationDate().isBefore(now.plusDays(IMMINENT_DAYS)))
                .count();

        return new ExpirySummary(plenty, imminent, expired, inventories.size(), IMMINENT_DAYS);
    }

    private LocationSummary buildLocationSummary(List<Inventory> inventories) {
        Map<String, Long> countMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getLocation().name(),
                        Collectors.counting()
                ));

        return new LocationSummary(
                countMap.getOrDefault("FRIDGE", 0L),
                countMap.getOrDefault("FREEZER", 0L),
                countMap.getOrDefault("PANTRY", 0L)
        );
    }

    private static final List<Category> ETC_CATEGORIES = List.of(
            Category.FAMINE, Category.EGG, Category.TOFU,
            Category.CANDRY, Category.FRZCONV, Category.GRAINS, Category.MSG
    );

    public List<InventoryResponse> getItems(Long memberId, String category) {
        List<Inventory> inventories = fridgeStatusRepository.findAllByMemberIdAndStatus(memberId, IngredientStatus.ACTIVE);

        Stream<Inventory> stream = inventories.stream();

        if (category != null) {
            stream = switch (category.toUpperCase()) {
                case "채소", "과일" -> stream.filter(i -> i.getCategory() == Category.VEGEFRUIT);
                case "육류" -> stream.filter(i -> i.getCategory() == Category.MEAT);
                case "수산물" -> stream.filter(i -> i.getCategory() == Category.SEA);
                case "기타" -> stream.filter(i -> ETC_CATEGORIES.contains(i.getCategory()));
                default -> stream;
            };
        }

        return stream
                .sorted(Comparator.comparing(Inventory::getExpirationDate))
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    private String resolveStatusMessage(ExpirySummary expiry) {
        if (expiry.expired() > 0) {
            return "유통기한이 초과된 항목이 있어요";
        }
        if (expiry.imminent() > 0) {
            return "유통기한이 임박한 항목이 있어요";
        }
        return "유통기한이 넉넉해요";
    }
}