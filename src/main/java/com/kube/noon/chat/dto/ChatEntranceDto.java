package com.kube.noon.chat.dto;

import com.kube.noon.chat.domain.ChatroomMemberType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class ChatEntranceDto {

    private int chatEntranceId;
    private int chatroomId;
    private String chatroomMemberId;
    private ChatroomMemberType chatroomMemberType;
    private LocalDateTime chatroomEnteredTime;
    private Boolean kicked;
}
