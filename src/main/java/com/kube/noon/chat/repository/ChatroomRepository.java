package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.domain.ChatroomType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer> {
    // 기본적인 CRUD 메서드는 JpaRepository가 제공
    // 추가적인 커스텀 메서드를 정의할 수 있음


    /**
     * 주어진 buildingId에 속하고 activated가 true인 Chatroom을 조회합니다.
     *
     * @param buildingId 건물 ID
     * @return 해당 건물에 속한 활성화된 Chatroom 목록
     */
    @Query("""
            SELECT c FROM Chatroom c
            WHERE c.building.buildingId = :buildingId
                AND c.activated = TRUE
            """)
    List<Chatroom> findByBuildingId(int buildingId);


    /**
     * 주어진 buildingId와 chatroomName을 포함하는 활성화된 Chatroom을 조회합니다.
     *
     * @param buildingId 건물 ID
     * @param searchKeywordChatroom 검색할 채팅방 이름 키워드
     * @return 검색 조건에 맞는 활성화된 Chatroom 목록
     */
    @Query("""
            SELECT cr FROM Chatroom cr
            WHERE :buildingId = cr.building.buildingId
                AND cr.chatroomName LIKE CONCAT('%', :searchKeywordChatroom, '%')
                AND cr.activated = TRUE
            """)
    List<Chatroom> findByBuildingIdAndChatroomNameContaining(int buildingId, String searchKeywordChatroom);


    /**
     * chatroomName을 포함하는 활성화된 Chatroom을 조회합니다.
     *
     * @param searchKeywordChatroom 검색할 채팅방 이름 키워드
     * @return 검색 조건에 맞는 활성화된 Chatroom 목록
     */
    @Query("""
            SELECT c FROM Chatroom c
            WHERE c.chatroomName LIKE CONCAT('%', :searchKeywordChatroom, '%')
                AND c.activated = TRUE
            """)
    List<Chatroom> findByChatroomNameContaining(String searchKeywordChatroom);


    /**
     * chatroomName을 포함하는 활성화된 GROUP_CHATTING 타입의 Chatroom을 페이지네이션하여 조회합니다.
     *
     * @param chatroomName 검색할 채팅방 이름 키워드
     * @param pageable 페이지네이션 정보
     * @return 검색 조건에 맞는 활성화된 Chatroom 페이지
     */
    @Query("""
            SELECT cr FROM Chatroom cr
            WHERE cr.chatroomName LIKE CONCAT('%', :chatroomName, '%')
                AND cr.activated = TRUE
                AND cr.chatroomType = com.kube.noon.chat.domain.ChatroomType.GROUP_CHATTING
            """)
    Page<Chatroom> findByChatroomNameContaining(@Param("chatroomName") String chatroomName, Pageable pageable);


    /**
     * 주어진 chatroomId에 해당하는 Chatroom을 비활성화합니다.
     *
     * @param chatroomId 채팅방 ID
     */
    @Transactional
    @Modifying
    @Query("UPDATE Chatroom c SET c.activated = false WHERE c.chatroomId = :chatroomId")
    void deleteChatroomByChatroomId(int chatroomId);


    /**
     * 주어진 chatroomId에 해당하는 활성화된 Chatroom을 조회합니다.
     *
     * @param chatroomId 채팅방 ID
     * @return 해당 채팅방 ID에 맞는 활성화된 Chatroom
     */
    @Query("""
            SELECT c FROM Chatroom c
            WHERE c.chatroomId = :chatroomId
                AND c.activated = TRUE
            """)
    Chatroom findChatroomByChatroomId(int chatroomId);


    /**
     * GROUP_CHATTING 방을 모두 비활성화합니다.
     *
     * @param chatroomType 채팅방 타입 (예: GROUP_CHATTING)
     * @return 비활성화된 채팅방의 수
     */
    @Transactional
    @Modifying
    @Query("UPDATE Chatroom c SET c.activated = false WHERE c.chatroomType = :chatroomType")
    int deactivateByChatroomType(ChatroomType chatroomType);

}