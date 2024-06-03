package com.kube.noon.notification.dto;

import com.kube.noon.notification.domain.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class NotificationDto {
    private String receiverId;
    private String notificationText;
    private NotificationType notificationType;
}
