package com.kube.noon.notification.domain;

import com.kube.noon.notification.converter.NotificationTypeConverter;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    // TODO: receiverId -> receiver
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    @Column(name = "notification_text", nullable = false)
    private String notificationText;

    @Column(name = "notification_type", nullable = false)
    @Convert(converter = NotificationTypeConverter.class)
    private NotificationType notificationType;

    public Notification(String receiverId, String notificationText, NotificationType notificationType) {
        this.receiverId = receiverId;
        this.notificationText = notificationText;
        this.notificationType = notificationType;
    }
}
