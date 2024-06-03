package com.kube.noon.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSearchCriteriaDto {

    private String memberId;
    private String nickname;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String phoneNumber;
    private Boolean signedOff;

}
