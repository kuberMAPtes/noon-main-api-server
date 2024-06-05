package com.kube.noon.member.service.impl;

import com.kube.noon.common.badwordfiltering.BadWordFilterAgent;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.binder.MemberBinder;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 파라미터는 식별에 반드시 필요한 데이터만 받는다
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final FeedService feedService;
    private final BadWordFilterAgent badWordFilterAgent;
    private final String[] badWordSeparator = {
            "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+",
            "`", "~", "[", "]", "{", "}", ";", ":", "\'", "\",", "\\", ".", "/", "<", ">", "?"
    };
//    private final SettingService settingService;

    @Override
    public void addMember(AddMemberDto memberDto) {
        try {
            log.info("회원 추가 중 : DTO {}", memberDto);

            if (memberDto.getSocialSignUp()) {
                memberDto.setPwd("social_sign_up");
            }

            Member member = MemberBinder.INSTANCE.toMember(memberDto);

            memberRepository.addMember(member);
            log.info("회원 추가 성공 : DTO {}", memberDto);
        } catch (DataAccessException e) {
            throw new MemberUpdateException(String.format("회원 추가 실패 : %s", memberDto), e);
        }
    }

    @Override//팔로우 or 차단
    public void addMemberRelationship(MemberRelationshipDto dto) {
        try {
            log.info("회원 관계 추가 중 : DTO {}", dto);
            checkMemberisSignedOff(dto.getFromId());
            memberRepository
                    .findMemberById(dto.getFromId())
                    .ifPresent(member -> {
                        try {
                            MemberRelationship mr = MemberBinder.INSTANCE.toMemberRelationship(dto);
                            memberRepository
                                    .findMemberRelationship(mr.getFromMember().getMemberId(), mr.getToMember().getMemberId())
                                    .ifPresent(relationship -> {
                                        throw new MemberSecurityBreachException("이미 추가된 관계입니다.");
                                    });

                            if (member.getMemberId().equals(dto.getToId())) {
                                throw new MemberSecurityBreachException("자기 자신과의 관계는 추가할 수 없습니다.");
                            }
                            //중복 추가가 되지 않아야 함
                        } catch (DataAccessException e) {
                            throw new MemberSecurityBreachException("회원 관계 추가 실패 : " + dto);
                        }
                    });
            MemberRelationship memberRelationship = MemberBinder.INSTANCE.toMemberRelationship(dto);
            memberRepository.addMemberRelationship(memberRelationship);
            log.info("회원 관계 추가 성공 : DTO {}", memberRelationship);
        } catch (DataAccessException e) {
            throw new MemberRelationshipUpdateException(String.format("회원 관계 추가 실패 : %s", dto), e);
        }
    }


    @Override
    public Optional<Member> findMemberById(String memberId) {
        try {
            log.info("회원 찾는 중 ID: {}", memberId);
            Optional<Member> om = memberRepository.findMemberById(memberId);
            om.ifPresentOrElse(
                    (member) -> log.info("회원 찾기 성공 "),
                    () -> new MemberNotFoundException(String.format("회원 조회 실패 : %s", memberId)));
            return om;
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }


    @Override
    public Optional<MemberProfileDto> findMemberProfileById(String memberId) {
        try {
            log.info("회원 프로필 찾는 중 ID: {}", memberId);
            Member member = memberRepository.findMemberById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(String.format("회원 조회 실패 : %s", memberId)));
            List<FeedDto> feedDtoList = new ArrayList<FeedDto>();
//            feedDtoList = feedService.findFeedListByMemberId(memberId);

            MemberProfileDto memberProfileDto = MemberBinder.INSTANCE.toDto(member, MemberProfileDto.class);

//            om.setFeedDtoList(feedDtoList);

            log.info("회원 프로필 찾기 성공 : {}", memberProfileDto);
            return Optional.ofNullable(memberProfileDto);
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        try {
            log.info("회원 찾는 중 닉네임: {}", nickname);
            Optional<Member> op = memberRepository.findMemberByNickname(nickname);
            String memberId = op.orElseThrow().getMemberId();
            checkMemberisSignedOff(memberId);
            log.info("회원 찾기 성공 : {}", memberId);
            return op;
        } catch (DataAccessException e) {
            log.error("DB 접근 관련 문제 발생", e);
            throw e;
        }
    }

    @Override
    public Optional<Member> findMemberByPhoneNumber(String phoneNumber) {
        try {
            log.info("회원 찾는 중 전화번호: {}", phoneNumber);
            Optional<Member> op = memberRepository.findMemberByPhoneNumber(phoneNumber);
            String memberId = op.orElseThrow().getMemberId();
            checkMemberisSignedOff(memberId);
            log.info("회원 찾기 성공 : {} ", memberId);
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
            log.info("회원 업데이트 중");
            checkMemberisSignedOff(updateMemberDto.getMemberId());
            memberRepository.updateMember(MemberBinder.INSTANCE.toMember(updateMemberDto));
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

    /**
     * 차단해제할거면 dto의 타입에 차단 넣는다.
     *
     * @param dto
     */
    @Override
    public void deleteMemberRelationship(MemberRelationshipDto dto) {
        log.info("회원 관계 삭제 중 : {}", dto);
        checkMemberisSignedOff(dto.getFromId());
        MemberRelationship mr = MemberBinder.INSTANCE.toMemberRelationship(dto);
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
                        , badWordSeparator
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
