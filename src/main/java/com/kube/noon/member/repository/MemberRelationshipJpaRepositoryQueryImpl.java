package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.enums.RelationshipType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRelationshipJpaRepositoryQueryImpl implements MemberRelationshipJpaRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    public List<MemberRelationship> findFollowingList(String memberId) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;
        return queryFactory.selectFrom(ms)
                .where(ms.fromMember.memberId.eq(memberId)
                        .and(ms.relationshipType.eq(RelationshipType.FOLLOW)))
                .fetch();
    }

    public List<MemberRelationship> findFollowerList(String memberId) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;
        return queryFactory.selectFrom(ms)
                .where(ms.toMember.memberId.eq(memberId)
                        .and(ms.relationshipType.eq(RelationshipType.FOLLOW)))
                .fetch();
    }

    public List<MemberRelationship> findBlockingList(String memberId) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;
        return queryFactory.selectFrom(ms)
                .where(ms.fromMember.memberId.eq(memberId)
                        .and(ms.relationshipType.eq(RelationshipType.BLOCK)))
                .fetch();
    }

    public List<MemberRelationship> findBlockerList(String memberId) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;
        return queryFactory.selectFrom(ms)
                .where(ms.toMember.memberId.eq(memberId)
                        .and(ms.relationshipType.eq(RelationshipType.BLOCK)))
                .fetch();
    }
}
