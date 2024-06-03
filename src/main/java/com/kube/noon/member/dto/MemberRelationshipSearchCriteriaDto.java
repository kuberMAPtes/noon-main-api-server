package com.kube.noon.member.dto;

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
public class MemberRelationshipSearchCriteriaDto {

    @NonNull
    private String memberId;

    private boolean following;
    private boolean follower;
    private boolean blocking;
    private boolean blocker;

}
