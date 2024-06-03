package com.kube.noon.member.service.impl;

import com.kube.noon.member.binder.MemberBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.exception.MemberRelationshipUpdateException;
import com.kube.noon.member.exception.MemberSecurityBreachException;
import com.kube.noon.member.exception.MemberUpdateException;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;


    @Override
    public void addMember(AddMemberDto memberDto) {
        try {

            if(memberDto.isSocialSignUp()){
                memberDto.setPwd("social_sign_up");
            }

            Member member = MemberBinder.INSTANCE.toMember(memberDto);

            memberRepository.addMember(member);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 추가 실패 : %s", memberDto), e);
        }
    }

    @Override//팔로우 or 차단
    public void addMemberRelationship(AddMemberRelationshipDto dto) {
        try {
            memberRepository
                    .findMemberById(dto.getFromId())
                    .ifPresent(member -> {
                try {
                    memberRepository
                            .findMemberRelationship(member.getMemberId(),dto.getToId(),dto.getRelationshipType())
                            .ifPresent(relationship -> {
                                throw new MemberSecurityBreachException("이미 추가된 관계입니다.");
                            });

                    if (member.getMemberId().equals(dto.getToId())) {
                        throw new MemberSecurityBreachException("자기 자신과의 관계는 추가할 수 없습니다.");
                    }

                    if (member.isSignedOff()){
                        throw new MemberSecurityBreachException("탈퇴한 회원과의 관계는 추가할 수 없습니다.");
                    }

                    //중복 추가가 되지 않아야 함

                }catch (DataAccessException e){
                    throw new MemberSecurityBreachException("회원 관계 추가 실패 : " + dto);
                }
            });

            MemberRelationship memberRelationship = MemberBinder.INSTANCE.toMemberRelationship(dto);
            memberRepository.addMemberRelationship(memberRelationship);

        } catch (DataAccessException e) {
            throw new MemberRelationshipUpdateException(String.format("회원 관계 추가 실패 : %s", dto), e);
        }
    }



    @Override
    public Optional<Member> findMemberById(String memberId) {
        try {
            return Optional.ofNullable(memberRepository.findMemberById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(String.format("회원 조회 실패 : %s", memberId))));
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }


    @Override
    public Optional<MemberProfileDto> findMemberProfileById(String memberId) {
        try {
            Member member = memberRepository.findMemberById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(String.format("회원 조회 실패 : %s", memberId)));
            return Optional.ofNullable(MemberBinder.INSTANCE.toDto(member, MemberProfileDto.class));
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        try {
            return Optional.ofNullable(memberRepository.findMemberByNickname(nickname)
                    .orElseThrow(() -> new MemberNotFoundException(String.format("회원 조회 실패 : %s", nickname))));
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteriaDto) {
        try {
            return memberRepository.findMemberListByCriteria(criteriaDto);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteriaDto) {
        try {
            return memberRepository.findMemberRelationshipListByCriteria(criteriaDto);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }


    @Override
    public void updateMember(UpdateMemberDto updateMemberDto) {
        try {
            memberRepository.updateMember(MemberBinder.INSTANCE.toMember(updateMemberDto));
        } catch (DataAccessException e) {
            throw new MemberUpdateException("회원 업데이트 실패", e);
        }
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {
        try {
            memberRepository.updatePassword(memberId, newPassword);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 비밀번호 업데이트 실패! : %s", memberId), e);
        }
    }

    @Override
    public void updatePhoneNumber(String memberId, String newPassword) {
        try {
            memberRepository.updatePhoneNumber(memberId, newPassword);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 전화번호 업데이트 실패! : %s", memberId), e);
        }
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        try {
            memberRepository.updateMemberProfilePhoto(memberId, newProfilePhotoUrl);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 프로필 사진 업데이트 실패 : %s", memberId), e);
        }
    }

    @Override
    public void deleteMemberRelationship(String fromId, String toId, RelationshipType relationshipType) {
        try {
            memberRepository.deleteMemberRelationship(fromId,toId,relationshipType);
        } catch (DataAccessException e) {
            throw new MemberRelationshipUpdateException(String.format("회원 관계 삭제 실패"), e);
        }
    }

    @Override
    public void deleteMember(String memberId) {
        try {
            memberRepository.deleteMember(memberId);
    } catch (DataAccessException e) {
        throw new MemberUpdateException(String.format("회원 삭제 실패"), e);
    }
    }
}
