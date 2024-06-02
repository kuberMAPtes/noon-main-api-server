package com.kube.noon.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRelationshipSearchCriteriaDto {

    private String memberId;
    private boolean following;
    private boolean follower;
    private boolean blocking;
    private boolean blocker;

}
