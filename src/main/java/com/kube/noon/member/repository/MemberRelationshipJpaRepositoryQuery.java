package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;

import java.util.List;

public interface MemberRelationshipJpaRepositoryQuery {
    List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria);

}
