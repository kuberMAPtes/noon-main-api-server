package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberJpaRepositoryQuery {

    Page<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria, Pageable pageable);

}
