package com.kube.noon.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_entrance")
@Getter
@Setter
@ToString
public class ChatEntrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_entrance_id")
    private int chatEntranceId;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroomId;

    @Column(name = "chatroom_member_id", length = 20, nullable = false)
    private String chatroomMemberId;

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
