package com.kube.noon.chat.dto;

import com.kube.noon.chat.domain.ChatroomMemberType;
import com.kube.noon.member.domain.Member;
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
    private Member chatroomMember;
    private ChatroomMemberType chatroomMemberType;
    private LocalDateTime chatroomEnteredTime;
    private Boolean kicked;
}
