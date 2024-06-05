package com.kube.noon.notification.service.sender;

import com.kube.noon.member.domain.Member;
import com.kube.noon.notification.domain.NotificationType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationEmptyAgent implements NotificationTransmissionAgent {

    @Override
    public void send(Member receiver, String text, NotificationType notificationType) {
        log.info("알림이 전송됨");
    }
}
