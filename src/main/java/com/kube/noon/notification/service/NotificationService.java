package com.kube.noon.notification.service;

import com.kube.noon.notification.dto.NotificationDto;

public interface NotificationService {

    public void sendNotification(NotificationDto notification);

    public NotificationDto getNotification(String notificationId);
}
