package com.kube.noon.notification.domain;

import com.kube.noon.member.domain.Member;
import com.kube.noon.notification.converter.NotificationTypeConverter;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author PGD
 */
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

    @Column(name = "notification_text", nullable = false)
    private String notificationText;

    @Column(name = "notification_type", nullable = false)
    @Convert(converter = NotificationTypeConverter.class)
    private NotificationType notificationType;

    @JoinColumn(name = "receiver_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Member receiver;

    public Notification(Member receiver, String notificationText, NotificationType notificationType) {
        this.receiver = receiver;
        this.notificationText = notificationText;
        this.notificationType = notificationType;
    }
}
