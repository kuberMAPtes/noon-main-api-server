package com.kube.noon.member.service.impl;

import com.kube.noon.common.badwordfiltering.BadWordFilterAgent;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;
import com.kube.noon.member.exception.MemberNotFoundException;
import com.kube.noon.member.exception.MemberRelationshipUpdateException;
import com.kube.noon.member.exception.MemberSecurityBreachException;
import com.kube.noon.member.exception.MemberUpdateException;
import com.kube.noon.member.repository.MemberRepository;
import com.kube.noon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 *
 */
@Validated
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final FeedService feedService;
    private final BadWordFilterAgent badWordFilterAgent;

//    private final SettingService settingService;

    @Override
    public void addMember(AddMemberDto dto) {
        try {
            log.info("회원 추가 중 : DTO {}", dto);
            Member member = DtoEntityBinder.INSTANCE.toEntity(dto);
            System.out.println("서비스에서 member 검증" + member);

            memberRepository.findMemberById(member.getMemberId()).ifPresent(member->{

            });

            if (Boolean.TRUE.equals(dto.getSocialSignUp())) {
                dto.setPwd("social_sign_up");
            }
            memberRepository.addMember(member);
            log.info("회원 추가 성공 : DTO {}", dto);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 추가 실패 : %s", dto), e);
        }
    }

    @Override//팔로우 or 차단
    public void addMemberRelationship(AddMemberRelationshipDto dto) {
        try {
            log.info("회원 관계 추가 중 : DTO {}", dto);
            dto.setActivated(true);

            MemberRelationship memberRelationship = DtoEntityBinder.INSTANCE.toEntity(dto);

            memberRepository.findMemberRelationship(dto.getFromId(), dto.getToId())
                    .ifPresentOrElse(
                            mr -> {//관계가 있었던 경우
                                memberRepository.updateMemberRelationship(memberRelationship);

                            }, () -> {//관계가 없었던 경우
                                memberRepository.addMemberRelationship(memberRelationship);
                            }
                    );

            log.info("회원 관계 추가 성공 : DTO {}", memberRelationship);
        } catch (DataAccessException e) {
            throw new MemberRelationshipUpdateException(String.format("회원 관계 추가 실패 : %s", dto), e);
        }
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        try {
            log.info("회원 찾는 중 ID: {}", memberId);
            return memberRepository.findMemberById(memberId);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<MemberProfileDto> findMemberProfileById(String memberId) {
        try {
            log.info("회원 프로필 찾는 중 ID: {}", memberId);

            return Optional.ofNullable(
                    memberRepository.findMemberById(memberId).map(
                            member -> {
                                MemberProfileDto memberProfileDto = DtoEntityBinder.INSTANCE.toDto(member, MemberProfileDto.class);

                                memberProfileDto.setFeedDtoList(feedService.getFeedListByMember(memberId));

                                return DtoEntityBinder.INSTANCE.toDto(member, MemberProfileDto.class);

                            }).orElseGet(() -> {
                        log.info("회원이 없습니다");
                        return null;
                    }));

        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        try {
            log.info("회원 찾는 중 닉네임: {}", nickname);
            return memberRepository.findMemberByNickname(nickname);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberByPhoneNumber(String phoneNumber) {
        try {
            log.info("회원 찾는 중 전화번호: {}", phoneNumber);
            return memberRepository.findMemberByPhoneNumber(phoneNumber);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }


    @Override
    public Page<Member> findMemberListByAdmin(MemberSearchCriteriaDto criteriaDto, int page, int size) {
        try {
            log.info("회원 리스트 찾는 중 : {}", criteriaDto);
            return memberRepository.findMemberListByCriteria(criteriaDto,page,size);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<MemberRelationship> findMemberRelationship(String fromId, String toId) {
        try {
            checkMemberisSignedOff(fromId);
            return memberRepository.findMemberRelationship(fromId, toId);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }
    @Override
    public Page<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteriaDto,int page,int size) {
        try {
            checkMemberisSignedOff(criteriaDto.getMemberId());
            return memberRepository.findMemberRelationshipListByCriteria(criteriaDto,page,size);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }


    @Override
    public void updateMember(UpdateMemberDto updateMemberDto) {
        try {
            log.info("회원 업데이트 중");
            checkMemberisSignedOff(updateMemberDto.getMemberId());
            memberRepository.updateMember(DtoEntityBinder.INSTANCE.toEntity(updateMemberDto));
        } catch (DataAccessException e) {
            throw new MemberUpdateException("회원 업데이트 실패", e);
        }
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {
        try {
            log.info("회원 비밀번호 업데이트 중");
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
            log.info("회원 전화번호 업데이트 중 :  {}", memberId);
            checkMemberisSignedOff(memberId);
            memberRepository.updatePhoneNumber(memberId, newPassword);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 전화번호 업데이트 실패! : %s", memberId), e);
        }
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        try {
            log.info("회원 프로필 사진 업데이트 중 : {}", memberId);
            checkMemberisSignedOff(memberId);
            memberRepository.updateMemberProfilePhoto(memberId, newProfilePhotoUrl);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 프로필 사진 업데이트 실패 : %s", memberId), e);
        }
    }

    @Override
    public void updateDajungScore(String memberId, int dajungScore) {
        try {
            log.info("회원 다정점수 업데이트 중 : {}", memberId);
            checkMemberisSignedOff(memberId);
            memberRepository.updateMember(
                    memberRepository.findMemberById(memberId)
                            .map(member -> {
                                member.setDajungScore(dajungScore);
                                return member;
                            }).orElseThrow(() -> new MemberNotFoundException("회원이 없습니다.")));
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 다정점수 업데이트 실패 : %s", memberId), e);
        }
    }

    /**
     * 차단해제할거면 dto의 타입에 차단 넣는다.
     *
     * @param dto
     */
    @Override
    public void deleteMemberRelationship(DeleteMemberRelationshipDto dto) {
        log.info("회원 관계 삭제 중 : {}", dto);
        checkMemberisSignedOff(dto.getFromId());
        MemberRelationship mr = DtoEntityBinder.INSTANCE.toEntity(dto);
        mr.setActivated(false);
        memberRepository.updateMemberRelationship(mr);
        log.info("회원 관계 삭제 성공");
    }

    @Override
    public void deleteMember(String memberId) {
        log.info("회원 삭제 중 : {}", memberId);
        checkMemberisSignedOff(memberId);
        memberRepository.updateMember(Member
                .builder()
                .memberId(memberId)
                .signedOff(true)
                .build());
        log.info("회원 삭제 성공 : {}", memberId);
    }

    @Override
    public boolean checkNickname(String nickname) {

        memberRepository.findMemberByNickname(nickname)
                .ifPresent(member -> {
                    throw new MemberSecurityBreachException("닉네임이 중복됩니다.");
                });

        return false;
    }

    @Override
    public boolean checkMemberId(String memberId) {

        memberRepository.findMemberById(memberId)
                .ifPresent(member -> {
                    throw new MemberSecurityBreachException("아이디가 중복됩니다.");
                });

        return false;
    }

    @Override
    public boolean checkPassword(String memberId, String password) {

        memberRepository.findMemberById(memberId)
                .ifPresent(member -> {
                    if (!member.getPwd().equals(password)) {
                        throw new MemberSecurityBreachException("비밀번호가 일치하지 않습니다.");
                    }
                });

        return false;
    }

    @Override
    public boolean checkPhoneNumber(String phoneNumber) {

        memberRepository.findMemberByPhoneNumber(phoneNumber)
                .ifPresent(member -> {
                    throw new MemberSecurityBreachException("전화번호가 중복됩니다.");
                });

        return false;
    }

    @Override
    public boolean checkBadWord(String word) {
        return badWordFilterAgent.change(
                        word.replace("*", "")
                        , badWordFilterAgent.getBadWordSeparator()
                )
                .contains("*");
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
