package com.kube.noon.member.service.impl;

import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.service.FeedService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final FeedService feedService;
//    private final SettingService settingService;

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
            checkMemberisSignedOff(dto.getFromId());
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


            List<FeedDto> feedDtoList = new ArrayList<FeedDto>();
//            feedDtoList = feedService.findFeedListByMemberId(memberId);

//            Optional<MemberProfileDto> Om = MemberBinder.INSTANCE.toDto(member, MemberProfileDto.class)
//                    .setFeedDtoList(feedDtoList);

            return null;
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        try {
            Optional<Member> op = memberRepository.findMemberByNickname(nickname);
            checkMemberisSignedOff(op.orElseThrow().getMemberId());
            return op;
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteriaDto) {
        try {
            checkMemberisSignedOff(criteriaDto.getMemberId());
            return memberRepository.findMemberListByCriteria(criteriaDto);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteriaDto) {
        try {
            checkMemberisSignedOff(criteriaDto.getMemberId());
            return memberRepository.findMemberRelationshipListByCriteria(criteriaDto);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }


    @Override
    public void updateMember(UpdateMemberDto updateMemberDto) {
        try {
            checkMemberisSignedOff(updateMemberDto.getMemberId());
            memberRepository.updateMember(MemberBinder.INSTANCE.toMember(updateMemberDto));
        } catch (DataAccessException e) {
            throw new MemberUpdateException("회원 업데이트 실패", e);
        }
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {
        try {
            checkMemberisSignedOff(memberId);
            memberRepository.updatePassword(memberId, newPassword);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 비밀번호 업데이트 실패! : %s", memberId), e);
        }
    }

    //비밀번호 변경
    @Override
    public void updatePhoneNumber(String memberId, String newPassword) {
        try {
            checkMemberisSignedOff(memberId);
            memberRepository.updatePhoneNumber(memberId, newPassword);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 전화번호 업데이트 실패! : %s", memberId), e);
        }
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        try {
            checkMemberisSignedOff(memberId);
            memberRepository.updateMemberProfilePhoto(memberId, newProfilePhotoUrl);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 프로필 사진 업데이트 실패 : %s", memberId), e);
        }
    }

    @Override
    public void deleteMemberRelationship(String fromId, String toId, RelationshipType relationshipType) {
        try {
            checkMemberisSignedOff(fromId);
            memberRepository.deleteMemberRelationship(fromId,toId,relationshipType);
        } catch (DataAccessException e) {
            throw new MemberRelationshipUpdateException(String.format("회원 관계 삭제 실패"), e);
        }
    }

    @Override
    public void deleteMember(String memberId) {
        try {
            checkMemberisSignedOff(memberId);

//            memberRepository.memberId 관리자인지 확인하는 로직 필요

            memberRepository.deleteMember(memberId);
    } catch (DataAccessException e) {
        throw new MemberUpdateException(String.format("회원 삭제 실패"), e);
    }
    }

    private void checkMemberisSignedOff(String memberId) {
        memberRepository.findMemberById(memberId)
                .ifPresent(member -> {
                    if (member.getSignedOff()) {
                        throw new MemberSecurityBreachException("탈퇴한 회원입니다.");
                    }
                });
    }
}
