package com.kube.noon.chat.dto;

import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.domain.ChatroomType;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ChatroomSearchResponseDto {
    private final int chatroomId;
    private final String chatroomName;
    private final int participantCount;
    private final String buildingName;
    private final String roadAddr;
    private final String chatroomCreatorId;
    private final ChatroomType chatroomType;
    private final float chatroomMinTemp;

    public static ChatroomSearchResponseDto of(Chatroom chatroom) {
        return ChatroomSearchResponseDto.builder()
                .chatroomId(chatroom.getChatroomId())
                .chatroomName(chatroom.getChatroomName())
                .participantCount(chatroom.getChatEntranceList().size())
                .buildingName(chatroom.getBuilding().getBuildingName())
                .roadAddr(chatroom.getBuilding().getRoadAddr())
                .chatroomCreatorId(chatroom.getChatroomCreatorId())
                .chatroomType(chatroom.getChatroomType())
                .chatroomMinTemp(chatroom.getChatroomMinTemp())
                .build();
    }
}
