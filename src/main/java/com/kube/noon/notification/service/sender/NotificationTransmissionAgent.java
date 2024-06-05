package com.kube.noon.notification.service.sender;

import com.kube.noon.member.domain.Member;
import com.kube.noon.notification.domain.NotificationType;

public interface NotificationTransmissionAgent {

    public void send(Member receiver, String text, NotificationType notificationType);
}
