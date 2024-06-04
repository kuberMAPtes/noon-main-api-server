package com.kube.noon.member.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 만약에 nickname = "nickname"이라면 nickname_1, nickname_2, nickname_3, ... 전부 찾는다.
 * 만약에 nickname = "nickname", phoneNumber = "010-1234-5678"이라면 nickname_1, nickname_2, nickname_3, ... 전부 찾고,
 * phoneNumber = "010-1234-5678"인 것 또한 찾는다.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberSearchCriteriaDto {

    private String memberId;
    private String nickname;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String phoneNumber;
    private Boolean signedOff;

}
