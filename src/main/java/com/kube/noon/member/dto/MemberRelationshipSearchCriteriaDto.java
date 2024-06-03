package com.kube.noon.member.dto;

import lombok.*;

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
