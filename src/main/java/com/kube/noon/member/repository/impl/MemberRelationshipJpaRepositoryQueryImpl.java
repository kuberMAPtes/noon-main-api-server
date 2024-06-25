package com.kube.noon.member.repository.impl;

import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipCountDto;
import com.kube.noon.member.dto.memberRelationship.FindMemberRelationshipListByCriteriaDto;
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
    public FindMemberRelationshipListByCriteriaDto findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria, Pageable pageable) {

        log.info(criteria.toString());
        log.info(pageable.toString());

        QMemberRelationship ms = QMemberRelationship.memberRelationship;

        BooleanBuilder builder = new BooleanBuilder();


        if (Boolean.TRUE.equals(criteria.getFollowing())) {
            builder.or(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)).and(ms.activated.eq(true)));
        }
        if (Boolean.TRUE.equals(criteria.getFollower())){
            builder.or(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)).and(ms.activated.eq(true)));
        }
        if (Boolean.TRUE.equals(criteria.getBlocking())) {
            builder.or(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)).and(ms.activated.eq(true)));
        }
        if (Boolean.TRUE.equals(criteria.getBlocker())) {
            builder.or(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)).and(ms.activated.eq(true)));
        }

        List<MemberRelationship> results = queryFactory.selectFrom(ms)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info(results.toString());



        int followingCount = (int) queryFactory.selectFrom(ms)
                .where(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)).and(ms.activated.eq(true)))
                .fetchCount();

        int followerCount = (int) queryFactory.selectFrom(ms)
                .where(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)).and(ms.activated.eq(true)))
                .fetchCount();

        int blockingCount = (int) queryFactory.selectFrom(ms)
                .where(ms.fromMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)).and(ms.activated.eq(true)))
                .fetchCount();

        int blockerCount = (int) queryFactory.selectFrom(ms)
                .where(ms.toMember.memberId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)).and(ms.activated.eq(true)))
                .fetchCount();

        long total = followingCount + followerCount + blockingCount + blockerCount;

        log.info("Following count: {}", followingCount);
        log.info("Follower count: {}", followerCount);
        log.info("Blocking count: {}", blockingCount);
        log.info("Blocker count: {}", blockerCount);


        MemberRelationshipCountDto counts = new MemberRelationshipCountDto(followingCount, followerCount, blockingCount, blockerCount);
        Page<MemberRelationship> page = new PageImpl<>(results, pageable,total);

        return new FindMemberRelationshipListByCriteriaDto(page, counts);
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
