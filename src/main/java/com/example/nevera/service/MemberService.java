package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.mypage.*;
import com.example.nevera.entity.Member;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        boolean hasWish = wishRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId).isPresent();
        return ProfileResponse.from(member, hasWish);
    }

    @Transactional(readOnly = true)
    public NotificationSettingResponse getNotificationSetting(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return NotificationSettingResponse.from(member);
    }

    @Transactional
    public ProfileResponse updateNickname(Long memberId, NicknameRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateNickname(request.nickname());
        boolean hasWish = wishRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId).isPresent();
        return ProfileResponse.from(member, hasWish);
    }

    @Transactional
    public NotificationSettingResponse updateNotificationEnabled(Long memberId, NotificationEnabledRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateNotificationEnabled(request.notificationEnabled());
        return NotificationSettingResponse.from(member);
    }

    @Transactional
    public NotificationSettingResponse updateNotificationTime(Long memberId, NotificationTimeRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateNotificationTime(request.notificationHour(), request.notificationMinute());
        return NotificationSettingResponse.from(member);
    }

    @Transactional(readOnly = true)
    public OnboardingCompleteResponse getOnboardingCompletedStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        boolean changedNickname = member.isNicknameChanged();
        boolean hasWish = wishRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId).isPresent();
        return OnboardingCompleteResponse.from(changedNickname, hasWish);
    }
}
