package com.example.nevera.service;

import com.example.nevera.dto.notification.NotificationResponse;
import com.example.nevera.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final int PAGE_SIZE = 20;

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long memberId, int offset) {
        return notificationRepository.findByMemberIdWithOffset(memberId, offset, PAGE_SIZE)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
