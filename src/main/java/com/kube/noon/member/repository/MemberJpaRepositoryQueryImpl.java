package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.QMember;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepositoryQueryImpl implements MemberJpaRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria) {
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getMemberId() != null) {
            builder.and(member.memberId.containsIgnoreCase(criteria.getMemberId()));
        }
        if (criteria.getNickname() != null) {
            builder.and(member.nickname.containsIgnoreCase(criteria.getNickname()));
        }
        if (criteria.getStartTime() != null && criteria.getEndTime() != null) {
            builder.and(member.unlockTime.between(criteria.getStartTime(), criteria.getEndTime()));
        }
        if (criteria.getPhoneNumber() != null) {
            builder.and(member.phoneNumber.containsIgnoreCase(criteria.getPhoneNumber()));
        }
        if (criteria.getSignedOff() != null) {
            builder.and(member.signedOff.eq(criteria.getSignedOff()));
        }

        return queryFactory.selectFrom(member)
                .where(builder)
                .fetch();

    }
}
