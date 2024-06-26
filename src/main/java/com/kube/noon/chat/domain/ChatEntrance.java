package com.kube.noon.chat.domain;

import com.kube.noon.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_entrance")
@Getter
@Setter
@ToString(exclude = "chatroom")
public class ChatEntrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_entrance_id")
    private int chatEntranceId;

//    ////???////
//    @Column(name = "chatroom_id", nullable = false)
//    private int chatroomId;
//    ////////////

    // Chatroom 이 삭제될 때 ChatEntrance 도 삭제되도록
    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @OneToOne
    @JoinColumn(name = "chatroom_member_id", nullable = false)
    private Member chatroomMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "chatroom_member_type", nullable = false)
    private ChatroomMemberType chatroomMemberType = ChatroomMemberType.MEMBER;

    @Column(name = "chatroom_entered_time", nullable = false)
    private LocalDateTime chatroomEnteredTime = LocalDateTime.now();

    @Column(name = "kicked", nullable = false)
    private boolean kicked = false;

    @Column(name = "activated", nullable = false)
    private boolean activated = true;
}
