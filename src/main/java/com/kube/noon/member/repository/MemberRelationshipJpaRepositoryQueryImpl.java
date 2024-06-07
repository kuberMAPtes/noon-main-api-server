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


    /**
     * 다 선택하면 다 나오고, 하나 선택하면 하나만 나옴
     *
     * @param criteria
     * @return
     */
    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria) {
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
