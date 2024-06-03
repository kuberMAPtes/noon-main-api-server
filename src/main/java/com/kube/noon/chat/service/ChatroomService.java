package com.kube.noon.chat.service;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.dto.ChatroomDto;

import java.util.List;

public interface ChatroomService {

    // 채팅방 생성
    public ChatroomDto addChatroom(ChatroomDto requestChatroom) throws Exception;

    // 채팅방 입장
    public ChatEntrance enterChatroom(ChatroomDto requestChatroom) throws Exception;

    // 채팅방 1개 조회
    public ChatroomDto getChatroomByRoomId(String roomId) throws Exception;

    // 내 채팅방 목록 전체조회
    public List<ChatroomDto> getChatroomsByMemberId(String chatroomMemberId) throws Exception;

}
