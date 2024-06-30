package com.kube.noon.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatApplyDto {

    private int chatApplyId;
    private String applyMessage;
    private String rejectMessage;
    private String fromId; // applicant
    private String toId; // respondent
    private String chatroomType;
    private boolean activated; // 신청 수락
}
