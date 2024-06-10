package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRelationshipJpaRepositoryQuery {

    Page<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria, Pageable pageable);

    public List<MemberRelationship> findAllMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria);
}
