package com.kube.noon.member.dto.memberRelationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberRelationshipCountDto {
    private int followingCount;
    private int followerCount;
    private int blockingCount;
    private int blockerCount;
}
