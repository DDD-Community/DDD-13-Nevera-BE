package com.example.nevera.service;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.dto.home.HomeSummaryResponse;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.WishEntity;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.SavingsRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private HomeService homeService;

    private static final Long MEMBER_ID = 1L;

    private Member member(String nickname) {
        return Member.builder().id(MEMBER_ID).email("test@test.com").nickname(nickname).build();
    }

    private WishEntity wishEntity(String name, long amount) {
        Member m = member("식구");
        WishEntity wish = WishEntity.builder().member(m).name(name).amount(amount).build();
        wish.prePersist();
        return wish;
    }

    @Test
    @DisplayName("wish 없을 때 홈 요약 - wish 관련 필드는 null")
    void getHomeSummary_noWish() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member("식구")));
        given(savingsRecordRepository.sumCostByMemberIdAndStatus(MEMBER_ID, IngredientStatus.CONSUMED)).willReturn(0L);
        given(savingsRecordRepository.sumCostByMemberIdAndStatus(MEMBER_ID, IngredientStatus.WASTED)).willReturn(0L);
        given(wishService.getCurrentWish(MEMBER_ID)).willReturn(Optional.empty());

        HomeSummaryResponse result = homeService.getHomeSummary(MEMBER_ID);

        assertThat(result.nickname()).isEqualTo("식구");
        assertThat(result.wishId()).isNull();
        assertThat(result.accumulated()).isNull();
        assertThat(result.totalConsumed()).isEqualTo(0L);
        assertThat(result.totalWasted()).isEqualTo(0L);
    }

    @Test
    @DisplayName("wish 있을 때 홈 요약 - 누적/남은 금액 계산 포함")
    void getHomeSummary_withWish() {
        WishEntity wish = wishEntity("노트북", 1_500_000L);
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member("테스터")));
        given(savingsRecordRepository.sumCostByMemberIdAndStatus(MEMBER_ID, IngredientStatus.CONSUMED)).willReturn(200_000L);
        given(savingsRecordRepository.sumCostByMemberIdAndStatus(MEMBER_ID, IngredientStatus.WASTED)).willReturn(50_000L);
        given(wishService.getCurrentWish(MEMBER_ID)).willReturn(Optional.of(wish));
        given(wishService.accumulatedAmount(wish)).willReturn(300_000L);

        HomeSummaryResponse result = homeService.getHomeSummary(MEMBER_ID);

        assertThat(result.nickname()).isEqualTo("테스터");
        assertThat(result.wishName()).isEqualTo("노트북");
        assertThat(result.wishAmount()).isEqualTo(1_500_000L);
        assertThat(result.accumulated()).isEqualTo(300_000L);
        assertThat(result.remaining()).isEqualTo(1_200_000L);
        assertThat(result.achieved()).isFalse();
        assertThat(result.totalConsumed()).isEqualTo(200_000L);
        assertThat(result.totalWasted()).isEqualTo(50_000L);
    }

    @Test
    @DisplayName("누적 금액이 목표 금액 초과 시 remaining은 0")
    void getHomeSummary_remainingIsZeroWhenOverAchieved() {
        WishEntity wish = wishEntity("노트북", 1_000_000L);
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member("식구")));
        given(savingsRecordRepository.sumCostByMemberIdAndStatus(eq(MEMBER_ID), any())).willReturn(0L);
        given(wishService.getCurrentWish(MEMBER_ID)).willReturn(Optional.of(wish));
        given(wishService.accumulatedAmount(wish)).willReturn(1_200_000L);

        HomeSummaryResponse result = homeService.getHomeSummary(MEMBER_ID);

        assertThat(result.remaining()).isEqualTo(0L);
    }
}
