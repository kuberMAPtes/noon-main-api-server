package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer> {
    // 기본적인 CRUD 메서드는 JpaRepository가 제공
    // 추가적인 커스텀 메서드를 정의할 수 있음
    List<Chatroom> findByBuildingId(int buildingId);
    List<Chatroom> findByBuildingIdAndChatroomNameContaining(int buildingId, String searchKeywordChatroom);
    List<Chatroom> findByChatroomNameContaining(String searchKeywordChatroom);

}
