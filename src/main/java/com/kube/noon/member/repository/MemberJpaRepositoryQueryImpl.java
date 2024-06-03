package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.QMember;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
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

        return queryFactory.selectFrom(member)
                .where(builder)
                .fetch();

    }

    @Override
    public void updateMember(Member member){
        QMember qm = QMember.member;

        JPAUpdateClause updateClause = queryFactory.update(qm);

        if(member.getNickname() != null) {
            updateClause.set(qm.nickname, member.getNickname());
        }
        if(member.getUnlockTime() != null) {
            updateClause.set(qm.unlockTime, member.getUnlockTime());
        }
        if(member.getProfilePhotoUrl() != null) {
            updateClause.set(qm.profilePhotoUrl, member.getProfilePhotoUrl());
        }
        if(member.getProfileIntro() != null) {
            updateClause.set(qm.profileIntro, member.getProfileIntro());
        }
        if(member.getDajungScore() != null) {
            updateClause.set(qm.dajungScore, member.getDajungScore());
        }
        if (member.getBuildingSubscriptionPublicRange() != null) {
            updateClause.set(qm.buildingSubscriptionPublicRange, member.getBuildingSubscriptionPublicRange());
        }
        if (member.getAllFeedPublicRange() != null) {
            updateClause.set(qm.allFeedPublicRange, member.getAllFeedPublicRange());
        }
        if (member.getMemberProfilePublicRange() != null) {
            updateClause.set(qm.memberProfilePublicRange, member.getMemberProfilePublicRange());
        }
        if (member.getReceivingAllNotificationAllowed()!=null) {
            updateClause.set(qm.receivingAllNotificationAllowed, member.getReceivingAllNotificationAllowed());
        }
        // Execute the update if there are any fields to update
        if (!updateClause.isEmpty()) {
            updateClause.where(qm.memberId.eq(member.getMemberId())).execute();
        }
    }
}
