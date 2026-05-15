package com.example.nevera.service;

import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.entity.Inventory;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.Notification;
import com.example.nevera.repository.InventoryRepository;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirationNotificationService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter MM_DD = DateTimeFormatter.ofPattern("MM.dd");

    private final MemberRepository memberRepository;
    private final InventoryRepository inventoryRepository;
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;
    private final MessageSource messageSource;

    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void sendExpirationNotifications() {
        OffsetDateTime now = OffsetDateTime.now(KST);
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        log.info("유통기한 알림 스케줄러 실행 - {}:{}", currentHour, currentMinute);

        List<Member> targets = memberRepository.findAllByNotificationHourAndNotificationMinute(currentHour, currentMinute);
        if (targets.isEmpty()) {
            return;
        }

        Set<Long> targetMemberIds = targets.stream()
                .map(Member::getId)
                .collect(Collectors.toSet());

        for (int daysLeft : List.of(1, 3, 7)) {
            LocalDate targetDate = now.toLocalDate().plusDays(daysLeft);
            OffsetDateTime startOfDay = targetDate.atStartOfDay(KST).toOffsetDateTime();
            OffsetDateTime endOfDay = targetDate.plusDays(1).atStartOfDay(KST).toOffsetDateTime();

            List<Inventory> expiring = inventoryRepository
                    .findAllByStatusAndExpirationDateGreaterThanEqualAndExpirationDateLessThan(
                            IngredientStatus.ACTIVE, startOfDay, endOfDay);

            for (Inventory inv : expiring) {
                if (!targetMemberIds.contains(inv.getMember().getId())) {
                    continue;
                }

                String title = messageSource.getMessage(
                        "notification.expiration.title", null, Locale.KOREAN);
                String type = messageSource.getMessage(
                        "notification.expiration.type", null, Locale.KOREAN);
                String deeplink = messageSource.getMessage(
                        "notification.expiration.deeplink", new Object[]{inv.getId()}, Locale.KOREAN);
                String message = daysLeft == 1
                        ? messageSource.getMessage("notification.expiration.message.tomorrow",
                                new Object[]{inv.getName(), inv.getCost()}, Locale.KOREAN)
                        : messageSource.getMessage("notification.expiration.message.date",
                                new Object[]{inv.getName(), inv.getCost(),
                                        inv.getExpirationDate().atZoneSameInstant(KST).format(MM_DD)}, Locale.KOREAN);

                Notification notification = Notification.builder()
                        .member(inv.getMember())
                        .inventory(inv)
                        .title(title)
                        .message(message)
                        .deeplink(deeplink)
                        .type(type)
                        .build();
                notificationRepository.save(notification);

                fcmService.sendPushIfTokenExists(inv.getMember(), notification);
            }
        }

        log.info("유통기한 알림 발송 완료 - 대상 사용자: {}명", targets.size());
    }
}
