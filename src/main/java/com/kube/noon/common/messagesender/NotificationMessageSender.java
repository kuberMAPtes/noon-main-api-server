package com.kube.noon.common.messagesender;

import com.kube.noon.member.domain.Member;
import com.kube.noon.notification.domain.NotificationType;

public interface NotificationMessageSender extends MessageSender {

    public void send(Member receiver, String text, NotificationType notificationType);
}
