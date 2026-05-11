package com.example.nevera.service;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.dto.inventory.ConsumedWastedResponse;
import com.example.nevera.dto.savings.MainSummaryResponse;
import com.example.nevera.repository.SavingsRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final SavingsRecordRepository savingsRecordRepository;
    private final WishService wishService;

    @Transactional(readOnly = true)
    public List<ConsumedWastedResponse> getConsumed(Long memberId, int offset, int limit) {
        return savingsRecordRepository
                .findByMemberIdAndStatus(memberId, IngredientStatus.CONSUMED, PageRequest.of(offset / limit, limit))
                .stream()
                .map(r -> ConsumedWastedResponse.from(r.getInventory()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsumedWastedResponse> getWasted(Long memberId, int offset, int limit) {
        return savingsRecordRepository
                .findByMemberIdAndStatus(memberId, IngredientStatus.WASTED, PageRequest.of(offset / limit, limit))
                .stream()
                .map(r -> ConsumedWastedResponse.from(r.getInventory()))
                .toList();
    }

    @Transactional(readOnly = true)
    public MainSummaryResponse getWeeklySummary(Long memberId) {
        OffsetDateTime[] week = currentWeekRange();
        int current = netSavings(memberId, week[0], week[1]);
        int previous = netSavings(memberId, week[0].minusWeeks(1), week[0]);
        return new MainSummaryResponse(current, changePercent(current, previous), wishService.getCurrent(memberId).orElse(null));
    }

    @Transactional(readOnly = true)
    public MainSummaryResponse getMonthlySummary(Long memberId) {
        OffsetDateTime[] month = currentMonthRange();
        int current = netSavings(memberId, month[0], month[1]);
        int previous = netSavings(memberId, month[0].minusMonths(1), month[0]);
        return new MainSummaryResponse(current, changePercent(current, previous), wishService.getCurrent(memberId).orElse(null));
    }

    private int netSavings(Long memberId, OffsetDateTime from, OffsetDateTime to) {
        int consumed = savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(memberId, IngredientStatus.CONSUMED, from, to);
        int wasted = savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(memberId, IngredientStatus.WASTED, from, to);
        return consumed - wasted;
    }

    private int changePercent(int current, int previous) {
        if (previous == 0) return 0;
        return (current - previous) * 100 / Math.abs(previous);
    }

    // 이번 주 월요일 00:00 ~ 다음 주 월요일 00:00
    private OffsetDateTime[] currentWeekRange() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime from = now.with(ChronoField.DAY_OF_WEEK, 1).toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();
        OffsetDateTime to = from.plusWeeks(1);
        return new OffsetDateTime[]{from, to};
    }

    // 이번 달 1일 00:00 ~ 다음 달 1일 00:00
    private OffsetDateTime[] currentMonthRange() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime from = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();
        OffsetDateTime to = from.plusMonths(1);
        return new OffsetDateTime[]{from, to};
    }
}
