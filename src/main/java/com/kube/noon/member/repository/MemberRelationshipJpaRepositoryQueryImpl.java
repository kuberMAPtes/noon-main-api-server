package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRelationshipJpaRepositoryQueryImpl implements MemberRelationshipJpaRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.isFollowing()) {
            builder.or(ms.fromId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (criteria.isFollower()) {
            builder.or(ms.toId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (criteria.isBlocking()) {
            builder.or(ms.fromId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }
        if (criteria.isBlocker()) {
            builder.or(ms.toId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }

        return queryFactory.selectFrom(ms)
                .where(builder)
                .fetch();
    }

}
