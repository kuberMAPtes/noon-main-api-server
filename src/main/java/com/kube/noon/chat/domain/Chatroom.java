package com.kube.noon.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "chatroom")
@Getter
@Setter
@ToString
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private int chatroomId;

    @Column(name = "chatroom_creator_id", length = 20, nullable = false)
    private String chatroomCreatorId;

    @Column(name = "building_id", nullable = false)
    private int buildingId;

    @Column(name = "chatroom_name", length = 50, nullable = false)
    private String chatroomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "chatroom_type", nullable = false)
    private ChatroomType chatroomType;

    @Column(name = "activated", nullable = false)
    private boolean activated = true;

    @Column(name = "chatroom_min_temp")
    private Float chatroomMinTemp;
}