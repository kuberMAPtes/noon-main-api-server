package com.kube.noon.chat.repository;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatEntranceRepository extends JpaRepository<ChatEntrance, Integer> {

    // 채팅멤버 ID를 기반으로 채팅멤버를 불러옴(추방이 되지 않은 채팅방 회원들만 불러옴)
    @Query("SELECT ce FROM ChatEntrance ce WHERE ce.chatroomMemberId = :chatroomMemberId AND ce.kicked = false")
    List<ChatEntrance> getChatEntrancesByChatroomMemberId(String chatroomMemberId);

    // 채팅방의 채팅멤버를 불러옴(추방이 되지 않은 유저들만 불러온다)
    @Query("SELECT ce FROM ChatEntrance ce WHERE ce.chatroom = :chatroom AND ce.kicked = false")
    List<ChatEntrance> findChatEntrancesByChatroom(Chatroom chatroom);

    // 아직 공부안댐 (Commented by GDP)
    @Query("""
        SELECT ce FROM ChatEntrance ce
        WHERE ce.chatroom.chatroomId = :chatroomId
        """)
    List<ChatEntrance> findChatEntranceListByChatroomId(Integer chatroomId);

    // 채팅방ID와 채팅방멤버ID에 해당하는 chatEntrance 의 kicked 속성을 0으로 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE ChatEntrance ce SET ce.kicked = true WHERE ce.chatroom = :chatroom AND ce.chatroomMemberId = :chatroomMemberId")
    int kickMember(@Param("chatroom") Chatroom chatroom, @Param("chatroomMemberId") String chatroomMemberId);

    // kicked 속성이 0인 ChatEntrance를 조회
    List<ChatEntrance> findByChatroomAndKickedFalse(Chatroom chatroom);

}
