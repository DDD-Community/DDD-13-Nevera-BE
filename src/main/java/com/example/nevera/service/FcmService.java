package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.notification.FcmSendRequest;
import com.example.nevera.entity.FcmToken;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.Notification;
import com.example.nevera.entity.NotificationFailure;
import com.example.nevera.repository.FcmTokenRepository;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.NotificationFailureRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter CREATED_AT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;
    private final NotificationFailureRepository notificationFailureRepository;

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
    public void sendNotification(Long memberId, FcmSendRequest.Data data) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.isNotificationEnabled()) {
            throw new BusinessException(ErrorCode.NOTIFICATION_DISABLED);
        }

        FcmToken fcmToken = fcmTokenRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.FCM_TOKEN_NOT_FOUND));

        Message.Builder messageBuilder = Message.builder()
                .setToken(fcmToken.getToken())
                .putData("title", data.title())
                .putData("message", data.message());
        putIfPresent(messageBuilder, "id", data.id());
        messageBuilder.putData("createdAt", OffsetDateTime.now(KST).format(CREATED_AT_FORMATTER));
        putIfPresent(messageBuilder, "deeplink", data.deeplink());
        putIfPresent(messageBuilder, "type", data.type());

        try {
            FirebaseMessaging.getInstance().send(messageBuilder.build());
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                fcmTokenRepository.delete(fcmToken);
                throw new BusinessException(ErrorCode.FCM_TOKEN_INVALID);
            }
            log.error("FCM 전송 실패 memberId={} errorCode={} message={}",
                    memberId, e.getMessagingErrorCode(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.FCM_SEND_ERROR);
        }
    }

    private void putIfPresent(Message.Builder builder, String key, String value) {
        if (value != null) {
            builder.putData(key, value);
        }
    }

    public void sendPushIfTokenExists(Member member, Notification notification) {
        fcmTokenRepository.findByMember(member).ifPresent(fcmToken -> {
            Message message = Message.builder()
                    .setToken(fcmToken.getToken())
                    .putData("title", notification.getTitle())
                    .putData("message", notification.getMessage())
                    .putData("createdAt", notification.getCreatedAt().atZoneSameInstant(KST).format(CREATED_AT_FORMATTER))
                    .putData("id", notification.getId().toString())
                    .putData("deeplink", notification.getDeeplink())
                    .putData("type", "default")
                    .build();
            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    fcmTokenRepository.delete(fcmToken);
                }
                log.warn("FCM 전송 실패 memberId={} notificationId={}", member.getId(), notification.getId());
                notificationFailureRepository.save(NotificationFailure.builder()
                        .member(member)
                        .inventory(notification.getInventory())
                        .notification(notification)
                        .reason(e.getMessage())
                        .build());
            }
        });
    }
}
