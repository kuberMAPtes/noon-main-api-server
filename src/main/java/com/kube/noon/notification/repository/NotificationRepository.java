package com.kube.noon.notification.repository;

import com.kube.noon.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author PGD
 */
public interface NotificationRepository
        extends JpaRepository<Notification, Integer> {

    @Query("SELECT nf FROM Notification nf WHERE nf.receiver.memberId = :receiverId")
    List<Notification> findNotificationListByReceiverId(String receiverId);
}
