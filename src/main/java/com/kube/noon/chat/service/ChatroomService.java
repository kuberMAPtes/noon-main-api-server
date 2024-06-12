package com.kube.noon.chat.service;

import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.dto.ChatEntranceDto;

import java.util.List;

public interface ChatroomService {

    /**
     * 채팅방 생성 (동시에 생성자를 채팅 참여멤버에 추가)
     * @param requestChatroom
     * @return 생성된 채팅방 정보가 담긴 ChatroomDto 를 반환
     * @throws Exception
     */
    public ChatroomDto addChatroom(ChatroomDto requestChatroom) throws Exception;

    /**
     * 채팅방 삭제 (관리자 기능)
     */

    /**
     * 채팅방에 참여한 멤버 리스트를 채팅방으로 조회
     * @param requestChatroom
     * @return 참여 멤버 리스트를 반환
     * @throws Exception
     */
    public List<ChatEntranceDto> getChatEntranceListByChatroom(ChatroomDto requestChatroom) throws Exception;

    /**
     * 채팅방에 참여한 멤버 리스트를 채팅방이름으로 조회
     * @param chatroomID
     * @return 참여 멤버 리스트를 반환
     * @throws Exception
     */
    public List<ChatEntranceDto> getChatEntranceListbyRoomId(int chatroomID) throws Exception;

    /**
     * 채팅방 ID로 해당 채팅방을 조회
     * @param chatroomID
     * @return
     * @throws Exception
     */
    public ChatroomDto getChatroomByRoomId(int chatroomID) throws Exception;


}
