package com.kube.noon.chat.dto;

import com.kube.noon.chat.domain.ChatroomMemberType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ChatApplyDto {

    private int chatApplyId;
    private String applyMessage;
    private String rejectMessage;
    private String fromId; // applicant
    private String toId; // respondent
    private boolean accepted; // 신청 수락
}
