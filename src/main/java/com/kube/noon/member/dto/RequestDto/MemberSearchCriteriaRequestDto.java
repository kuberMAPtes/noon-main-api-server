package com.kube.noon.member.dto.RequestDto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberSearchCriteriaRequestDto {

    private String fromId;
    private String memberId;
    private String nickname;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String phoneNumber;
    private Boolean signedOff;

}
