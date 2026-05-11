package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.entity.FcmToken;
import com.example.nevera.entity.Member;
import com.example.nevera.repository.FcmTokenRepository;
import com.example.nevera.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveToken(Long memberId, String token) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        fcmTokenRepository.findByMember(member)
                .ifPresentOrElse(
                        fcmToken -> fcmToken.updateToken(token),
                        () -> fcmTokenRepository.save(FcmToken.builder()
                                .member(member)
                                .token(token)
                                .build())
                );
    }

    @Transactional
    public void sendNotification(Long memberId, String title, String body) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        FcmToken fcmToken = fcmTokenRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.FCM_TOKEN_NOT_FOUND));

        Message message = Message.builder()
                .setToken(fcmToken.getToken())
                .putData("title", title)
                .putData("body", body)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                fcmTokenRepository.delete(fcmToken);
                throw new BusinessException(ErrorCode.FCM_TOKEN_INVALID);
            }
            throw new BusinessException(ErrorCode.FCM_SEND_ERROR);
        }
    }
}
