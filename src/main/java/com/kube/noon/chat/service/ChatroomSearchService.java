package com.kube.noon.chat.service;

import com.kube.noon.chat.dto.ChatroomDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatroomSearchService {

    /**
     * 건물별 채팅방 목록 조회
     * @return
     * @throws Exception
     */
    public List<ChatroomDto> getBuildingChatroomList(int buildingId) throws Exception;

    /**
     * 건물별 채팅방 목록 검색
     * @param buildingId
     * @param searchKeywordChatroom
     * @return
     * @throws Exception
     */
    public List<ChatroomDto> getBuildingChatroomListByChatroomName(int buildingId, String searchKeywordChatroom) throws Exception;

    /**
     * 내 채팅방 목록 조회 (멤버ID로 채팅방 목록을 조회)
     * @param chatroomMemberId
     * @return
     * @throws Exception
     */
    public List<ChatroomDto> getChatroomListByMemberId(String chatroomMemberId) throws Exception;

    /**
     * 채팅방 이름으로 채팅방 목록 조회 (관리자 기능)
     * @param chatroomName
     * @return
     * @throws Exception
     */
    public List<ChatroomDto> getChatroomListByChatroomName(String chatroomName) throws Exception;

    public Page<ChatroomDto> searchChatroomByChatroomName(String searchKeyword, int page);

    /**
     * 활발한 채팅방 조회
     * @return
     * @throws Exception
     */
    public List<ChatroomDto> getLivelistChatroomList(int buildingId) throws Exception;
}
