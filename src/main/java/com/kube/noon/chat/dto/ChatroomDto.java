package com.kube.noon.chat.dto;

import com.kube.noon.chat.domain.ChatroomType;
import com.kube.noon.member.domain.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class ChatroomDto {

    private int chatroomID;
    private Float chatroomMinTemp;
    private String chatroomName;
    private String chatroomCreatorId;
    private Member chatroomCreator;
    private ChatroomType chatroomType;
    private int buildingId;
    
    private String invitedMemberId;
    private int chatroomEntrancesSize; // 채팅방 참여멤버수를 채팅방에 저장
}
