package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRelationshipJpaRepositoryQuery {


    public List<MemberRelationship> findFollowingList(String memberId, Pageable pageable);

    public List<MemberRelationship> findFollowerList(String memberId, Pageable pageable);

    public List<MemberRelationship> findBlockingList(String memberId, Pageable pageable);

    public List<MemberRelationship> findBlockerList(String memberId, Pageable pageable);

    List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria, Pageable pageable);
}
