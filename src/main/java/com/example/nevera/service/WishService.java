package com.example.nevera.service;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.wish.WishRequest;
import com.example.nevera.dto.wish.WishResponse;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.WishEntity;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.SavingsRecordRepository;
import com.example.nevera.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final SavingsRecordRepository savingsRecordRepository;

    @Transactional
    public WishResponse register(Long memberId, WishRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        wishRepository.deleteAllByMemberId(memberId);

        WishEntity wish = WishEntity.builder()
                .member(member)
                .name(request.name())
                .amount(request.amount())
                .build();

        return WishResponse.from(wishRepository.save(wish));
    }

    @Transactional(readOnly = true)
    public Optional<WishEntity> getCurrentWish(Long memberId) {
        return wishRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional(readOnly = true)
    public Optional<WishResponse> getCurrent(Long memberId) {
        return getCurrentWish(memberId).map(WishResponse::from);
    }

    @Transactional
    public WishResponse update(Long memberId, Long wishId, WishRequest request) {
        WishEntity wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_NOT_FOUND));

        if (!wish.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.WISH_FORBIDDEN);
        }

        if (wish.isAchieved()) {
            throw new BusinessException(ErrorCode.WISH_ALREADY_ACHIEVED);
        }

        wish.update(request.name(), request.amount());

        long accumulated = accumulatedAmount(wish);
        if (accumulated >= wish.getAmount()) {
            wish.achieve();
        }

        return WishResponse.from(wish);
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        WishEntity wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_NOT_FOUND));

        if (!wish.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.WISH_FORBIDDEN);
        }

        wishRepository.delete(wish);
    }

    public long accumulatedAmount(WishEntity wish) {
        return savingsRecordRepository.sumCostByMemberIdAndStatusFrom(
                wish.getMember().getId(), IngredientStatus.CONSUMED, wish.getCreatedAt());
    }
}
