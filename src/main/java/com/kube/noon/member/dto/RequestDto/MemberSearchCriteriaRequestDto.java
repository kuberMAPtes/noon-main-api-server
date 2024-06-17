package com.kube.noon.member.dto.RequestDto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberSearchCriteriaRequestDto {

    //fromId가
    private String fromId;
    //memberId라는 조건으로 LIKE 검색
    private String memberId;
    private String nickname;
    private int pageSize;
    private int pageUnit;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String phoneNumber;
    private Boolean signedOff;


}
