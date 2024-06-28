package com.kube.noon.chat.service;

import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.dto.ChatEntranceDto;
import com.kube.noon.member.domain.Member;

import java.util.List;
import java.util.Map;

public interface ChatroomService {

    /**
     * 채팅방 생성 (동시에 생성자를 채팅 참여멤버에 추가)
     * @param requestChatroom
     * @return 생성된 채팅방 정보가 담긴 ChatroomDto 를 반환
     * @throws Exception
     */
    public ChatroomDto addChatroom(ChatroomDto requestChatroom) throws Exception;

    /**
     * 채팅방 입장시키기
     * @param roomId
     * @param memberId
     * @return 채팅방 멤버목록을 리턴
     * @throws Exception
     */
    public ChatEntranceDto enterChatroom(int roomId, String memberId) throws Exception;
    
    /**
     * 채팅방 삭제 (관리자 기능)
     * @param chatroomId
     * @return
     * @throws Exception
     */
    public String deleteChatroom(int chatroomId) throws Exception;

    /**
     * Job 의 내용을 일정주기로 반복실행하는 스케쥴러, ChatroomSchedularConfig 에서 정의한 DeleteChatroomJob의 실제 실행 내용
     * @return
     * @throws Exception
     */
    public int scheduledDeleteGroupChatrooms() throws Exception;

    /**
     * 채팅방 추방
     * @param chatroomId
     * @param memberId
     * @return
     * @throws Exception
     */
    public Map<String, Object> kickChatroom(int chatroomId, String memberId) throws Exception;

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
