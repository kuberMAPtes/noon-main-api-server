package com.kube.noon.member.repository;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
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
public class MemberRelationshipJpaRepositoryQueryImpl implements MemberRelationshipJpaRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria, Pageable pageable) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getFollowing() != null) {
            builder.and(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (criteria.getFollower() != null) {
            builder.and(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (criteria.getBlocking() != null) {
            builder.and(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }
        if (criteria.getBlocker() != null) {
            builder.and(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }

        List<MemberRelationship> results = queryFactory.selectFrom(ms)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(ms)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }


}
