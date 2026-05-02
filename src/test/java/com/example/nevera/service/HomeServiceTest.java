package com.example.nevera.service;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.dto.savings.MainSummaryResponse;
import com.example.nevera.dto.wish.WishResponse;
import com.example.nevera.repository.SavingsRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {

    @Mock
    private SavingsRecordRepository savingsRecordRepository;

    @Mock
    private WishService wishService;

    @InjectMocks
    private HomeService homeService;

    private static final Long MEMBER_ID = 1L;

    // ── wish 없을 때 ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("wish 없을 때 주간 요약 조회 - wish는 null, 절감액은 0")
    void getWeeklySummary_noWish() {
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.CONSUMED), any(), any())).willReturn(0);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.WASTED), any(), any())).willReturn(0);
        given(wishService.getCurrent(MEMBER_ID)).willReturn(Optional.empty());

        MainSummaryResponse result = homeService.getWeeklySummary(MEMBER_ID);

        assertThat(result.netSavings()).isEqualTo(0);
        assertThat(result.changePercent()).isEqualTo(0);
        assertThat(result.wish()).isNull();
    }

    @Test
    @DisplayName("wish 없을 때 월간 요약 조회 - wish는 null, 절감액은 0")
    void getMonthlySummary_noWish() {
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.CONSUMED), any(), any())).willReturn(0);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.WASTED), any(), any())).willReturn(0);
        given(wishService.getCurrent(MEMBER_ID)).willReturn(Optional.empty());

        MainSummaryResponse result = homeService.getMonthlySummary(MEMBER_ID);

        assertThat(result.netSavings()).isEqualTo(0);
        assertThat(result.changePercent()).isEqualTo(0);
        assertThat(result.wish()).isNull();
    }

    // ── wish 있을 때 ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("wish 있을 때 주간 요약 조회 - wish 정보가 포함된다")
    void getWeeklySummary_withWish() {
        WishResponse wish = new WishResponse("노트북", 1_500_000);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.CONSUMED), any(), any())).willReturn(0);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.WASTED), any(), any())).willReturn(0);
        given(wishService.getCurrent(MEMBER_ID)).willReturn(Optional.of(wish));

        MainSummaryResponse result = homeService.getWeeklySummary(MEMBER_ID);

        assertThat(result.wish()).isNotNull();
        assertThat(result.wish().name()).isEqualTo("노트북");
        assertThat(result.wish().amount()).isEqualTo(1_500_000);
    }

    @Test
    @DisplayName("wish 있을 때 월간 요약 조회 - wish 정보가 포함된다")
    void getMonthlySummary_withWish() {
        WishResponse wish = new WishResponse("노트북", 1_500_000);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.CONSUMED), any(), any())).willReturn(0);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.WASTED), any(), any())).willReturn(0);
        given(wishService.getCurrent(MEMBER_ID)).willReturn(Optional.of(wish));

        MainSummaryResponse result = homeService.getMonthlySummary(MEMBER_ID);

        assertThat(result.wish()).isNotNull();
        assertThat(result.wish().name()).isEqualTo("노트북");
        assertThat(result.wish().amount()).isEqualTo(1_500_000);
    }

    // ── 절감액 및 변화율 계산 ──────────────────────────────────────────────────

    @Test
    @DisplayName("이전 기간 데이터 없으면 changePercent는 0")
    void changePercent_previousIsZero() {
        // 이번 주 consumed=10000, wasted=0 / 지난 주 둘 다 0
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.CONSUMED), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .willReturn(10_000, 0);  // 이번주, 지난주 순서
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.WASTED), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .willReturn(0, 0);
        given(wishService.getCurrent(MEMBER_ID)).willReturn(Optional.empty());

        MainSummaryResponse result = homeService.getWeeklySummary(MEMBER_ID);

        assertThat(result.changePercent()).isEqualTo(0);
    }

    @Test
    @DisplayName("절감액 증가 시 양수 changePercent 반환 - 이전 10000 → 현재 12000이면 20%")
    void changePercent_positive() {
        // consumed 호출 순서: 이번주, 지난주, 이번주(wasted), 지난주(wasted)
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.CONSUMED), any(), any()))
                .willReturn(12_000, 10_000);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.WASTED), any(), any()))
                .willReturn(0, 0);
        given(wishService.getCurrent(MEMBER_ID)).willReturn(Optional.empty());

        MainSummaryResponse result = homeService.getWeeklySummary(MEMBER_ID);

        assertThat(result.netSavings()).isEqualTo(12_000);
        assertThat(result.changePercent()).isEqualTo(20);
    }

    @Test
    @DisplayName("절감액 감소 시 음수 changePercent 반환 - 이전 10000 → 현재 8000이면 -20%")
    void changePercent_negative() {
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.CONSUMED), any(), any()))
                .willReturn(8_000, 10_000);
        given(savingsRecordRepository.sumCostByMemberIdAndStatusAndPeriod(
                eq(MEMBER_ID), eq(IngredientStatus.WASTED), any(), any()))
                .willReturn(0, 0);
        given(wishService.getCurrent(MEMBER_ID)).willReturn(Optional.empty());

        MainSummaryResponse result = homeService.getWeeklySummary(MEMBER_ID);

        assertThat(result.netSavings()).isEqualTo(8_000);
        assertThat(result.changePercent()).isEqualTo(-20);
    }
}
