package com.kube.noon.member.repository.impl;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.repository.MemberRelationshipJpaRepositoryQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRelationshipJpaRepositoryQueryImpl implements MemberRelationshipJpaRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria, Pageable pageable) {

        log.info(criteria.toString());
        log.info(pageable.toString());

        QMemberRelationship ms = QMemberRelationship.memberRelationship;

        BooleanBuilder builder = new BooleanBuilder();


        if (Boolean.TRUE.equals(criteria.getFollowing())) {
            builder.or(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (Boolean.TRUE.equals(criteria.getFollower())){
            builder.or(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (Boolean.TRUE.equals(criteria.getBlocking())) {
            builder.or(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }
        if (Boolean.TRUE.equals(criteria.getBlocker())) {
            builder.or(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }

        List<MemberRelationship> results = queryFactory.selectFrom(ms)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info(results.toString());

        long total = queryFactory.selectFrom(ms)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<MemberRelationship> findAllMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria) {
        QMemberRelationship ms = QMemberRelationship.memberRelationship;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getFollowing() != null) {
            builder.or(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (criteria.getFollower() != null) {
            builder.or(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)));
        }
        if (criteria.getBlocking() != null) {
            builder.or(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }
        if (criteria.getBlocker() != null) {
            builder.or(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)));
        }

        return queryFactory.selectFrom(ms)
                .where(builder)
                .fetch();
    }


}
