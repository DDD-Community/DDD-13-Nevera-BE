package com.example.nevera.service;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.home.HomeSummaryResponse;
import com.example.nevera.dto.inventory.ConsumedWastedResponse;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.WishEntity;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.SavingsRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final SavingsRecordRepository savingsRecordRepository;
    private final WishService wishService;
    private final MemberRepository memberRepository;

    @Transactional
    public HomeSummaryResponse getHomeSummary(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        long totalConsumed = savingsRecordRepository.sumCostByMemberIdAndStatus(memberId, IngredientStatus.CONSUMED);
        long totalWasted = savingsRecordRepository.sumCostByMemberIdAndStatus(memberId, IngredientStatus.WASTED);

        Optional<WishEntity> wishOpt = wishService.getCurrentWish(memberId);
        if (wishOpt.isEmpty()) {
            return new HomeSummaryResponse(
                    member.getNickname(),
                    null, null, null, null, null, null,
                    totalConsumed, totalWasted
            );
        }

        WishEntity wish = wishOpt.get();
        long accumulated = wishService.accumulatedAmount(wish);

        if (!wish.isAchieved() && accumulated >= wish.getAmount()) {
            wish.achieve();
        }

        long remaining = Math.max(0L, wish.getAmount() - accumulated);

        return new HomeSummaryResponse(
                member.getNickname(),
                wish.getId(),
                wish.getName(),
                wish.getAmount(),
                accumulated,
                remaining,
                wish.isAchieved(),
                totalConsumed,
                totalWasted
        );
    }

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
}
