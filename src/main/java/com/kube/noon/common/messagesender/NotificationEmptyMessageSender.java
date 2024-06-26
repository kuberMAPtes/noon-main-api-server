package com.kube.noon.common.messagesender;

import com.kube.noon.member.domain.Member;
import com.kube.noon.notification.domain.NotificationType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationEmptyMessageSender implements NotificationMessageSender {

    @Override
    public void send(Member receiver, String text, NotificationType notificationType) {
        log.info("알림이 전송됨");
    }

    @Override
    public void send(Member receiver, String text) {
        log.info("메시지가 전송됨");
    }
}
