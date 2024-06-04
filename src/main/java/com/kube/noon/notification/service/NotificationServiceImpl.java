package com.kube.noon.notification.service;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.service.MemberService;
import com.kube.noon.notification.domain.Notification;
import com.kube.noon.notification.dto.NotificationDto;
import com.kube.noon.notification.repository.NotificationRepository;
import com.kube.noon.notification.service.sender.NotificationTransmissionAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTransmissionAgent transmissionAgent;
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    @Override
    public void sendNotification(NotificationDto dto) {
        Member receiver = this.memberService.findMemberById(dto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("그런 회원 없습니다: " + dto.getReceiverId())); // TODO: 구체적인 예외
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
}
