package com.kube.noon.member.dto.RequestDto;

import lombok.*;


/**
 * Boolean이 null 이면 값이 들어가지 않음
 * Boolean이 true 이면 값이 들어감
 * Boolean이 false 이면 값이 들어가지 않음
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberRelationshipSearchCriteriaRequestDto {

    @NonNull
    private String fromId;
    private String memberId;
    private Boolean following;
    private Boolean follower;
    private Boolean blocking;
    private Boolean blocker;

}
