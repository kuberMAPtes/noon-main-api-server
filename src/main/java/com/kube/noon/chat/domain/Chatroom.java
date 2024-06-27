package com.kube.noon.chat.domain;

import com.kube.noon.building.domain.Building;
import com.kube.noon.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chatroom_creator_id", referencedColumnName = "member_id", nullable = false)
    private Member chatroomCreator; // Chatroom이 Member를 참조하도록 수정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", referencedColumnName = "building_id", nullable = true)
    private Building building; // Chatroom이 Building을 참조하도록 수정

    @Column(name = "chatroom_name", length = 50, nullable = false)
    private String chatroomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "chatroom_type", nullable = false)
    private ChatroomType chatroomType;

    @Column(name = "activated", nullable = false)
    private boolean activated = true;

    @Column(name = "chatroom_dajung_temp_min")
    private Float chatroomMinTemp;

    @OneToMany(mappedBy = "chatroom", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatEntrance> chatEntranceList;
}