package com.kube.noon.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberSearchCriteriaDto {

    private String memberId;
    private String nickname;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String phoneNumber;
    private Boolean signedOff;

}
