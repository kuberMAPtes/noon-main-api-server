package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
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

    @Query("""
            SELECT c FROM Chatroom c
            WHERE c.building.buildingId = :buildingId
                AND c.activated = TRUE
            """)
    List<Chatroom> findByBuildingId(int buildingId);

    @Query("""
            SELECT cr FROM Chatroom cr
            WHERE :buildingId = cr.building.buildingId
                AND cr.chatroomName LIKE CONCAT('%', :searchKeywordChatroom, '%')
                AND cr.activated = TRUE
            """)
    List<Chatroom> findByBuildingIdAndChatroomNameContaining(int buildingId, String searchKeywordChatroom);

    @Query("""
            SELECT c FROM Chatroom c
            WHERE c.chatroomName LIKE CONCAT('%', :searchKeywordChatroom, '%')
                AND c.activated = TRUE
            """)
    List<Chatroom> findByChatroomNameContaining(String searchKeywordChatroom);

    @Query("""
            SELECT cr FROM Chatroom cr
            WHERE cr.chatroomName LIKE CONCAT('%', :chatroomName, '%')
                AND cr.activated = TRUE
                AND cr.chatroomType = com.kube.noon.chat.domain.ChatroomType.GROUP_CHATTING
            """)
    Page<Chatroom> findByChatroomNameContaining(@Param("chatroomName") String chatroomName, Pageable pageable);

    void deleteChatroomByChatroomId(int chatroomId);
    Chatroom findChatroomByChatroomId(int chatroomId);
}