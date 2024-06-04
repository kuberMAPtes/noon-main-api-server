package com.kube.noon.notification.service;

import com.kube.noon.notification.domain.Notification;
import com.kube.noon.notification.dto.NotificationDto;
import com.kube.noon.notification.repository.NotificationRepository;
import com.kube.noon.notification.service.sender.NotificationTransmissionAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTransmissionAgent transmissionAgent;
    private final NotificationRepository notificationRepository;

    @Override
    public void sendNotification(NotificationDto notification) {

    }

    @Override
    public NotificationDto getNotification(String notificationId) {
        return null;
    }
}
