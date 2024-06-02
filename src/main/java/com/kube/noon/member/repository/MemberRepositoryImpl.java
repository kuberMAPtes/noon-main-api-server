package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMember;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository,MemberJpaRepositoryQuery{
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRelationshipJpaRepository memberRelationshipJpaRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public void addMember(Member member) {
        memberJpaRepository.save(member);
    }

    @Override
    public void addMemberRelationship(MemberRelationship memberRelationship) {
        memberRelationshipJpaRepository.save(memberRelationship);
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        return memberJpaRepository.findMemberById(memberId);
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        return memberJpaRepository.findMemberByNickname(nickname);
    }


    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria) {
        criteria.setSignedOff(true);
        QMember member = QMember.member;

//        SELECT * FROM member
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
                        criteria.isFollowing() ? ms.fromId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)): null,
                        criteria.isFollower() ? ms.toId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.FOLLOW)): null,
                        criteria.isBlocking() ? ms.fromId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)): null,
                        criteria.isBlocker() ? ms.toId.eq(criteria.getMemberId()).and(ms.relationshipType.eq(RelationshipType.BLOCK)): null
                ).fetch();
    }

    @Override
    public void updateMember(Member member) {
        memberJpaRepository.save(member);
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {

    }

    @Override
    public void updatePhoneNumber(String memberId, String newPassword) {

    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {

    }

    @Override
    public void deleteMemberRelationship(String memberRelationshipId) {

    }

    @Override
    public void deleteMember(String memberId) {

    }
}
