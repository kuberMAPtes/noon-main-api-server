package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;

public interface MemberRelationshipRepository {

    void addMemberRelationship(MemberRelationship memberRelationship);
    void deleteMemberRelationship(int memberRelationshipId);
}
