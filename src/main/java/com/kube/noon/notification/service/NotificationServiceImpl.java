package com.kube.noon.notification.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.notification.domain.Notification;
import com.kube.noon.notification.dto.NotificationDto;
import com.kube.noon.notification.repository.NotificationRepository;
import com.kube.noon.common.messagesender.NotificationMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMessageSender transmissionAgent;
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    @Override
    public void sendNotification(NotificationDto dto) {
        Member receiver;
        try {
            receiver = this.memberService.findMemberById(dto.getReceiverId())
                    .orElseThrow(() -> new NoSuchElementException("그런 회원 없습니다: " + dto.getReceiverId()));
        } catch (NoSuchElementException e) {
            log.warn("대상 회원이 존재하지 않음", e);
            return;
        }
        if (receiver.getReceivingAllNotificationAllowed() == null || !receiver.getReceivingAllNotificationAllowed()) {
            return;
        }
        saveNotification(receiver, dto);
        this.transmissionAgent.send(receiver, dto.getNotificationText(), dto.getNotificationType());
    }

    private void saveNotification(Member receiver, NotificationDto dto) {

        Notification notification =
                new Notification(receiver, dto.getNotificationText(), dto.getNotificationType());
        this.notificationRepository.save(notification);
    }

    @Override
    public NotificationDto getNotification(int notificationId) {
        Notification notification = this.notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("그런 알림 ID 없습니다: " + notificationId));// TODO: 구체적인 예외
        return NotificationDto.from(notification);
    }

    @Override
    public List<NotificationDto> getNotificationList(String receiverId) {
        return this.notificationRepository.findNotificationListByReceiverId(receiverId)
                .stream()
                .map(NotificationDto::from)
                .toList();
    }
}
