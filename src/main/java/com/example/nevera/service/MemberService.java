package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.mypage.NotificationSettingRequest;
import com.example.nevera.dto.mypage.NotificationSettingResponse;
import com.example.nevera.dto.mypage.NotificationTimeRequest;
import com.example.nevera.entity.Member;
import com.example.nevera.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public NotificationSettingResponse getNotificationSetting(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return NotificationSettingResponse.from(member);
    }

    @Transactional
    public NotificationSettingResponse updateNotificationEnabled(Long memberId, NotificationSettingRequest request) {
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
}
