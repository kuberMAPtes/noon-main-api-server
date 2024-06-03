package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMember;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberJpaRepositoryQueryImpl implements MemberJpaRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    public MemberJpaRepositoryQueryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria) {
        QMember member = QMember.member;

        return queryFactory.selectFrom(member)
                .where(
                        criteria.getMemberId() != null ? member.memberId.containsIgnoreCase(criteria.getMemberId()) : null,
                        criteria.getNickname() != null ? member.nickname.containsIgnoreCase(criteria.getNickname()) : null,
                        criteria.getStartTime() != null && criteria.getEndTime() != null ? member.unlockTime.between(criteria.getStartTime(), criteria.getEndTime()) : null,
                        criteria.getPhoneNumber() != null ? member.phoneNumber.containsIgnoreCase(criteria.getPhoneNumber()) : null,
                        criteria.getSignedOff() != null ? member.signedOff.eq(criteria.getSignedOff()) : null
                )
                .fetch();
    }

    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;

        return queryFactory.selectFrom(ms)
                .where(
                        criteria.isFollowing() ? ms.fromId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)) : null,
                        criteria.isFollower() ? ms.toId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)) : null,
                        criteria.isBlocking() ? ms.fromId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)) : null,
                        criteria.isBlocker() ? ms.toId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)) : null
                ).fetch();
    }
}
