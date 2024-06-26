package com.kube.noon.member.repository.impl;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.QMember;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.repository.MemberJpaRepositoryQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepositoryQueryImpl implements MemberJpaRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    public Page<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria, Pageable pageable) {
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getMemberId() != null) {
            builder.or(member.memberId.containsIgnoreCase(criteria.getMemberId()));
        }
        if (criteria.getNickname() != null) {
            builder.or(member.nickname.containsIgnoreCase(criteria.getNickname()));
        }
        if (criteria.getStartTime() != null && criteria.getEndTime() != null) {
            builder.or(member.unlockTime.between(criteria.getStartTime(), criteria.getEndTime()));
        }
        if (criteria.getPhoneNumber() != null) {
            builder.or(member.phoneNumber.containsIgnoreCase(criteria.getPhoneNumber()));
        }
        if (criteria.getSignedOff() != null) {
            builder.or(member.signedOff.eq(criteria.getSignedOff()));
        }

        List<Member> results = queryFactory.selectFrom(member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(member)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
