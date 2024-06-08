package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatEntranceRepository extends JpaRepository<ChatEntrance, Integer> {
    List<ChatEntrance> getChatEntrancesByChatroomMemberId(String chatroomMemberId);
    List<ChatEntrance> findChatEntrancesByChatroom(Chatroom chatroom);

    // 아직 공부안댐 (Commented by GDP)
    @Query("""
        SELECT ce FROM ChatEntrance ce
        WHERE ce.chatroom.chatroomId = :chatroomId
        """)
    List<ChatEntrance> findChatEntranceListByChatroomId(Integer chatroomId);


}
