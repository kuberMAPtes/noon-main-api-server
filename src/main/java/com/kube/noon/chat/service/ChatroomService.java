package com.kube.noon.chat.service;

import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.dto.ChatEntranceDto;

import java.util.List;

public interface ChatroomService {

    // 채팅방 생성 (use)
    public ChatroomDto addChatroom(ChatroomDto requestChatroom) throws Exception;

    // '채팅방 참여멤버' 를 채팅방으로 조회
    public List<ChatEntranceDto> getChatEntranceListByChatroom(ChatroomDto requestChatroom) throws Exception;

    // '채팅방 참여멤버' 를 채팅방ID로 조회 (sub)
    public List<ChatEntranceDto> getChatEntranceListbyRoomId(int chatroomID) throws Exception;

    // '채팅방 목록' 을 회원ID로 조회
    public List<ChatroomDto> getChatroomListByMemberId(String chatroomMemberId) throws Exception;

    // '채팅방' 에 대한 정보를 채팅방ID로 조회
    public ChatroomDto getChatroomByRoomId(int chatroomID) throws Exception;


}
