package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;

import java.util.List;

public interface MemberRelationshipJpaRepositoryQuery {


    public List<MemberRelationship> findFollowingList(String memberId);

    public List<MemberRelationship> findFollowerList(String memberId);

    public List<MemberRelationship> findBlockingList(String memberId);

    public List<MemberRelationship> findBlockerList(String memberId);


}
