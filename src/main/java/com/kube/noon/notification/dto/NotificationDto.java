package com.kube.noon.notification.dto;

import com.kube.noon.notification.domain.Notification;
import com.kube.noon.notification.domain.NotificationType;
import lombok.*;

/**
 * @author PGD
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@ToString
public class NotificationDto {
    private String receiverId;
    private String notificationText;
    private NotificationType notificationType;

    public static NotificationDto from(Notification notification) {
        return NotificationDto.builder()
                .receiverId(notification.getReceiver().getMemberId())
                .notificationText(notification.getNotificationText())
                .notificationType(notification.getNotificationType())
                .build();
    }
}
