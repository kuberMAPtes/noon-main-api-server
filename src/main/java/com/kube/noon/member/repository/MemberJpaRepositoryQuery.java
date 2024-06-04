package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;

import java.util.List;

public interface MemberJpaRepositoryQuery {

    List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria);

}
