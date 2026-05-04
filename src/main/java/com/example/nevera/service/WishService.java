package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.wish.WishRequest;
import com.example.nevera.dto.wish.WishResponse;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.WishEntity;
import com.example.nevera.repository.MemberRepository;
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
    public Optional<WishResponse> getCurrent(Long memberId) {
        return wishRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .map(WishResponse::from);
    }

    @Transactional
    public WishResponse update(Long memberId, Long wishId, WishRequest request) {
        WishEntity wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_NOT_FOUND));

        if (!wish.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.WISH_FORBIDDEN);
        }

        wish.update(request.name(), request.amount());
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
}
