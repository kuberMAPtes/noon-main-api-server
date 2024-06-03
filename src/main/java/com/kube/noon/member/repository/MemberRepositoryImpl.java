package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.exception.MemberRelationshipUpdateException;
import com.kube.noon.member.exception.MemberUpdateException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRelationshipJpaRepository memberRelationshipJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JPAQueryFactory queryFactory;

    @Override
    public void addMember(Member member) {
        try {
            log.info("회원 추가 중 : {}", member);
            memberJpaRepository.save(member);
            log.info("회원 추가 성공 : {}", member);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 추가 실패 : %s",member),e);
        }
    }

    @Override
    public void addMemberRelationship(MemberRelationship memberRelationship) {
        try {
            log.info("회원 관계 추가 중 : {}", memberRelationship);
            memberRelationshipJpaRepository.save(memberRelationship);
            log.info("회원 관계 추가 성공 : {}", memberRelationship);
        } catch (DataAccessException e) {
            throw new MemberRelationshipUpdateException(String.format("회원 관계 추가 실패 : %s",memberRelationship),e);
        }
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        try {
            log.info("회원 찾는 중 ID: {}", memberId);
            Optional<Member> op =  memberJpaRepository.findMemberByMemberId(memberId);
            op.ifPresentOrElse(
                    member -> log.info("회원 찾기 성공 : {}",member),
                    ()->log.info("회원 찾기 실패 : 해당 ID의 회원이 없음")
            );
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        return memberJpaRepository.findMemberByNickname(nickname);
    }

    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria) {
        return memberJpaRepository.findMemberListByCriteria(criteria);
    }

    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria) {
        return memberJpaRepository.findMemberRelationshipListByCriteria(criteria);
    }

    @Override
    public void updateMember(Member member) {
        memberJpaRepository.save(member);
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {

        memberJpaRepository.findMemberByMemberId(memberId).ifPresentOrElse(
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
            memberJpaRepository.findMemberByMemberId(memberId).ifPresentOrElse(
                    member -> {
                        member.setPhoneNumber(newPhoneNumber);
                        memberJpaRepository.save(member);
                        log.info("회원 전화번호 업데이트 성공! :  {}", memberId);
                    }, () -> {
                        throw new MemberNotFoundException(String.format("ID로 회원을 찾을 수 없음! :: %s", memberId));

                    }
            );
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 전화번호 업데이트 실패! : %s", memberId), e);
        }
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        try {
            log.info("회원 프로필 사진 업데이트 중 : {}", memberId);
            memberJpaRepository.findMemberByMemberId(memberId).ifPresentOrElse(
                    member -> {
                        member.setProfilePhotoUrl(newProfilePhotoUrl);
                        memberJpaRepository.save(member);
                    }, () -> {
                        throw new MemberNotFoundException(String.format("ID로 회원을 찾을 수 없음 : %s", memberId));
                    }
            );
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 프로필 사진 업데이트 실패 : %s", memberId), e);
        }
    }

    @Override
    public void deleteMemberRelationship(int memberRelationshipId) {
        try {
            log.info("회원 관계 삭제 중 : {}", memberRelationshipId);
            memberRelationshipJpaRepository.deleteById(memberRelationshipId);
            log.info("회원 관계 삭제 성공 : {}", memberRelationshipId);
        } catch (DataAccessException e) {
            throw new MemberRelationshipUpdateException(String.format("회원 관계 삭제 실패 : %s", memberRelationshipId), e);
        }

    }

    @Override
    public void deleteMember(String memberId) {
        try {
            log.info("회원 삭제 중 : {}", memberId);
            memberJpaRepository.deleteById(memberId);
            log.info("회원 삭제 성공 : {}", memberId);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 삭제 실패 : %s", memberId), e);
        }
    }
}
