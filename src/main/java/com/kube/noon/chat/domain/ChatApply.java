package com.kube.noon.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "chat_apply")
@Entity
@Getter
@Setter
@ToString
public class ChatApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_apply_id", nullable = false)
    private Integer chatApplyId;

    /**
     *  JoinColumn 이지만 임시로 Column으로 처리
     */
    @Column(name = "applicant_id", nullable = false)
    private String applicant;

    /**
     *  JoinColumn 이지만 임시로 Column으로 처리
     */
    @Column(name = "respondent_id", nullable = false)
    private String respondent;

    @Column(name = "apply_message", length = 400)
    private String applyMessage;

    @Column(name = "reject_message", length = 400)
    private String rejectMessage;

    //해야됨.. accepted 된거는 빼고 가져와야하니까
    @Column(name = "activated")
    private Boolean accepted;

}
