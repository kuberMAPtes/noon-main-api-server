package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatEntranceRepository extends JpaRepository<ChatEntrance, Integer> {
    List<ChatEntrance> getChatEntrancesByChatroomMemberId(String chatroomMemberId);
    List<ChatEntrance> findChatEntrancesByChatroom(Chatroom chatroom);

    // 기본적인 CRUD 메서드는 JpaRepository가 제공
    // 추가적인 커스텀 메서드를 정의할 수 있음

}
