package com.kube.noon.chat.exceptions;

import com.kube.noon.chat.domain.Chatroom;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ChatroomAutoDeleteFailException extends RuntimeException {

    public ChatroomAutoDeleteFailException(final String message) {
        super(message); // 부모 타입인 RuntimeException 의 메시지 생성자를 호출
    }

}

