package com.kube.noon.notification.repository;

import com.kube.noon.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<Notification, Integer> {
}
