package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.QMember;
import com.kube.noon.member.domain.QMemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.exception.MemberRelationshipUpdateException;
import com.kube.noon.member.exception.MemberUpdateException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl implements MemberRepository,MemberJpaRepositoryQuery{
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRelationshipJpaRepository memberRelationshipJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public void addMember(Member member) {
        log.info("Adding member: {}", member);
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

        memberJpaRepository.findMemberById(memberId).ifPresentOrElse(
                member -> {
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    member.setPwd(encryptedPassword);
                    memberJpaRepository.save(member);
                },
                () -> {
                    throw new IllegalArgumentException("Member not found");
                }

        );
    }

    @Override
    public void updatePhoneNumber(String memberId, String newPhoneNumber) {

        try {
            log.info("회원 전화번호 업데이트 중 :  {}", memberId);
            memberJpaRepository.findMemberById(memberId).ifPresentOrElse(
                    member -> {
                        member.setPhoneNumber(newPhoneNumber);
                        memberJpaRepository.save(member);
                        log.info("회원 전화번호 업데이트 성공! :  {}", memberId);
                    }, () -> {
                        throw new MemberNotFoundException(String.format("ID로 회원을 찾을 수 없음! :: %s", memberId));

                    }
            );
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 전화번호 업데이트 실패! : %s", memberId),e);
        }
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        try{
            log.info("회원 프로필 사진 업데이트 중 : {}", memberId);
            memberJpaRepository.findMemberById(memberId).ifPresentOrElse(
                    member -> {
                        member.setProfilePhotoUrl(newProfilePhotoUrl);
                        memberJpaRepository.save(member);
                    },()->{
                        throw new MemberNotFoundException(String.format("ID로 회원을 찾을 수 없음 : %s",memberId);
            }
            );
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 프로필 사진 업데이트 실패 : %s",memberId),e);
        }
    }

    @Override
    public void deleteMemberRelationship(int memberRelationshipId) {
        try {
            log.info("회원 관계 삭제 중 : {}",memberRelationshipId);
            memberRelationshipJpaRepository.deleteById(memberRelationshipId);
            log.info("회원 관계 삭제 성공 : {}",memberRelationshipId);
        } catch (DataAccessException e){
            throw new MemberRelationshipUpdateException(String.format("회원 관계 삭제 실패 : %s",memberRelationshipId),e);
        }

    }

    @Override
    public void deleteMember(String memberId) {
        try{
            log.info("회원 삭제 중 : {}", memberId);
            memberJpaRepository.deleteById(memberId);
            log.info("회원 삭제 성공 : {}",memberId);
        } catch (DataAccessException e){
            throw new MemberUpdateException(String.format("회원 삭제 실패 : %s",memberId),e);
        }
    }
}
