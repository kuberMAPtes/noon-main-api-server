package com.kube.noon.common.messagesender;

import com.kube.noon.member.domain.Member;

public interface MessageSender {

    public void send(Member receiver, String text);
}
