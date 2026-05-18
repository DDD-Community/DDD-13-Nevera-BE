package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import com.example.nevera.dto.notification.NotificationFailureResponse;
import com.example.nevera.entity.NotificationFailure;
import com.example.nevera.repository.NotificationFailureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationFailureService {

    private final NotificationFailureRepository notificationFailureRepository;
    private final FcmService fcmService;

    @Transactional(readOnly = true)
    public List<NotificationFailureResponse> getFailures() {
        return notificationFailureRepository.findAllByOrderByFailedAtDesc()
                .stream()
                .map(NotificationFailureResponse::from)
                .toList();
    }

    @Transactional
    public void retry(Long failureId) {
        NotificationFailure failure = notificationFailureRepository.findById(failureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_FAILURE_NOT_FOUND));

        fcmService.sendPushIfTokenExists(failure.getMember(), failure.getNotification());
        failure.incrementRetryCount();
    }
}
