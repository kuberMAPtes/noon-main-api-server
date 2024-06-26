package com.kube.noon.notification.service;

import com.kube.noon.notification.dto.NotificationDto;

import java.util.List;

/**
 * @author PGD
 */
public interface NotificationService {

    /**
     * 알림을 전송한다.
     * 전송된 알림은 데이터베이스에 저장된다.
     * @param notification 전송할 알림 데이터가 담긴 DTO 객체
     */
    public void sendNotification(NotificationDto notification);

    /**
     * 알림을 조회한다.
     * @param notificationId 알림의 ID
     * @return notificationId로 식별되는 알림 데이터 DTO 객체
     */
    public NotificationDto getNotification(int notificationId);

    /**
     * 특정 회원이 받은 알림을 조회한다.
     * @param receiverId 회원의 ID
     * @return receiverId로 식별되는 회원이 받은 알림 데이터 DTO 객체
     */
    public List<NotificationDto> getNotificationList(String receiverId);
}
