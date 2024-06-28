package com.kube.noon.member.repository.impl;

import com.kube.noon.common.PublicRange;
import com.kube.noon.common.constant.PagingConstants;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.memberRelationship.FindMemberRelationshipListByCriteriaDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.exception.*;
import com.kube.noon.member.repository.MemberJpaRepository;
import com.kube.noon.member.repository.MemberRelationshipJpaRepository;
import com.kube.noon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


/**
 * Repository에서는 받은 데이터는 1차적으로 서비스로부터 검증된 데이터이다.
 * 데이터의 일관성을 유지하기 위한 최소한의 검사를 수행한다.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRelationshipJpaRepository memberRelationshipJpaRepository;
//    private final PasswordEncoder passwordEncoder;

    @Override
    public void addMember(Member member) {
        try {
            member.setMemberRole(Role.MEMBER);
            member.setDajungScore(0);
            member.setSignedOff(false);
            member.setMemberProfilePublicRange(PublicRange.PUBLIC);
            member.setBuildingSubscriptionPublicRange(PublicRange.PUBLIC);
            member.setAllFeedPublicRange(PublicRange.PUBLIC);
            member.setReceivingAllNotificationAllowed(true);
            String dateTimeString = "0001-01-01 01:01:01";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime unlockTime = LocalDateTime.parse(dateTimeString, formatter);
            member.setUnlockTime(unlockTime);
            memberJpaRepository.save(member);
            log.info("회원 추가 성공: {}", member);
        } catch (DataAccessException e) {
            log.error("회원 추가 중 오류 발생", e);
            throw new MemberCreationException("회원 추가 중 오류 발생", e);
        }
    }

    @Override
    public void addMemberRelationship(MemberRelationship memberRelationship) {
        try {
            memberRelationshipJpaRepository.findByFromMember_MemberIdAndToMember_MemberId(
                    memberRelationship.getFromMember().getMemberId(),
                    memberRelationship.getToMember().getMemberId()
            ).ifPresent((relationship) -> {
                throw new MemberRelationshipCreationException("회원 관계가 이미 존재합니다");
            });
            memberRelationship.setActivated(true);
            log.info("INSERT 명령어 수행 : {}", memberRelationship);
            memberRelationshipJpaRepository.save(memberRelationship);
        } catch (DataAccessException e) {
            log.error("회원 관계 추가 중 오류 발생", e);
            throw new MemberRelationshipCreationException("회원 관계 추가 중 오류 발생", e);
        }
    }
    @Override
    public Optional<Member> findMemberById(String memberId) {
        try {
            return memberJpaRepository.findMemberByMemberId(memberId);
        } catch (DataAccessException e) {
            log.error("회원 ID로 조회 중 오류 발생", e);
            throw new MemberNotFoundException("회원 조회 중 오류 발생", e);
        }
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        try {
            return memberJpaRepository.findMemberByNickname(nickname);
        } catch (DataAccessException e) {
            log.error("닉네임으로 회원 조회 중 오류 발생", e);
            throw new MemberNotFoundException("회원 조회 중 오류 발생", e);
        }
    }

    @Override
    public Page<Member> findMemberByNickname(String nickname, String requester, int page) {
        if (page < 1) {
            return new PageImpl<>(List.of());
        }
        PageRequest pageRequest =
                PageRequest.of(page - 1, PagingConstants.PAGE_SIZE, Sort.by(Sort.Order.asc("nickname")));
        return this.memberJpaRepository.findMemberByNicknameLike(nickname, requester, pageRequest);
    }

    @Override
    public Optional<Member> findMemberByPhoneNumber(String phoneNumber) {
        try {
            return memberJpaRepository.findMemberByPhoneNumber(phoneNumber);
        } catch (DataAccessException e) {
            log.error("전화번호로 회원 조회 중 오류 발생", e);
            throw new MemberNotFoundException("회원 조회 중 오류 발생", e);
        }
    }

    @Override
    public Page<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria, int page, int size) {
        try {
            Page<Member> lm = memberJpaRepository.findMemberListByCriteria(criteria, PageRequest.of(page, size));
            if (lm.isEmpty()) {
                log.info("조건에 맞는 회원이 없음");
            } else {
                for (Member m : lm) {
                    log.info("memberId : {} 닉네임 :  {}", m.getMemberId(), m.getNickname());
                }
            }
            return lm;
        } catch (DataAccessException e) {
            log.error("회원 목록 조회 중 오류 발생", e);
            throw new MemberNotFoundException("회원 목록 조회 중 오류 발생", e);
        }
    }

    @Override
    public Optional<MemberRelationship> findMemberRelationship(String fromId, String toId) {
        try {
            Optional<MemberRelationship> omr = memberRelationshipJpaRepository.
                    findByFromMember_MemberIdAndToMember_MemberId(
                            fromId,
                            toId);
            omr.ifPresentOrElse(
                    memberRelationship -> log.info("회원 관계 찾기 성공 : {}", memberRelationship),
                    () -> log.info("회원 관계 찾기 실패 : 해당 관계가 없음")
            );
            return omr;
        } catch (DataAccessException e) {
            log.error("회원 관계 조회 중 오류 발생", e);
            throw new MemberRelationshipNotFoundException("회원 관계 조회 중 오류 발생", e);
        }
    }

    @Override
    public FindMemberRelationshipListByCriteriaDto findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria, int page, int size) {
        try {
            FindMemberRelationshipListByCriteriaDto dto = memberRelationshipJpaRepository.findMemberRelationshipListByCriteria(criteria, PageRequest.of(page, size));
            if (dto.getMemberRelationshipPage().getTotalElements()==0) {
                log.info("조건에 맞는 회원 관계가 없음");
            } else {
                for (MemberRelationship mr : dto.getMemberRelationshipPage().toList()) {
                    log.info("member_1의 FromId 리스트 출력 : {}", mr);
                }
            }
            return dto;
        } catch (DataAccessException e) {
            log.error("회원 관계 조회 중 오류 발생", e);
            throw new MemberRelationshipNotFoundException("회원 관계 조회 중 오류 발생", e);
        }
    }

    @Override
    public List<MemberRelationship> findAllMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria) {
        try {
            List<MemberRelationship> lm = memberRelationshipJpaRepository.findAllMemberRelationshipListByCriteria(criteria);
            if (lm.isEmpty()) {
                log.info("조건에 맞는 회원 관계가 없음");
            } else {
                for (MemberRelationship mr : lm) {
                    log.info("member_1의 FromId 리스트 출력 : {}", mr.getFromMember().getMemberId());
                }
                for (MemberRelationship mr : lm) {
                    log.info("member_1의 ToId 리스트 출력 : {}", mr.getToMember().getMemberId());
                }
            }
            return lm;
        } catch (DataAccessException e) {
            log.error("회원 관계 조회 중 오류 발생", e);
            throw new MemberRelationshipNotFoundException("회원 관계 조회 중 오류 발생", e);
        }
    }

    @Override
    public void updateMember(Member member) {
        try {
            Member newMember = memberJpaRepository.findMemberByMemberId(member.getMemberId())
                    .orElseThrow(() -> new MemberNotFoundException("Member not found"));

            if (member.getNickname() != null) {
                newMember.setNickname(member.getNickname());
            }
            if (member.getUnlockTime() != null) {
                newMember.setUnlockTime(member.getUnlockTime());
            }
            if (member.getProfilePhotoUrl() != null) {
                newMember.setProfilePhotoUrl(member.getProfilePhotoUrl());
            }
            if (member.getProfileIntro() != null) {
                newMember.setProfileIntro(member.getProfileIntro());
            }
            if (member.getDajungScore() != null) {
                newMember.setDajungScore(member.getDajungScore());
            }
            if (member.getSignedOff() != null) {
                newMember.setSignedOff(member.getSignedOff());
            }
            if (member.getBuildingSubscriptionPublicRange() != null) {
                newMember.setBuildingSubscriptionPublicRange(member.getBuildingSubscriptionPublicRange());
            }
            if (member.getAllFeedPublicRange() != null) {
                newMember.setAllFeedPublicRange(member.getAllFeedPublicRange());
            }
            if (member.getMemberProfilePublicRange() != null) {
                newMember.setMemberProfilePublicRange(member.getMemberProfilePublicRange());
            }
            if (member.getReceivingAllNotificationAllowed() != null) {
                newMember.setReceivingAllNotificationAllowed(member.getReceivingAllNotificationAllowed());
            }
            memberJpaRepository.save(newMember);
            log.info("회원 업데이트 성공");
        } catch (DataAccessException e) {
            log.error("회원 업데이트 중 오류 발생", e);
            throw new MemberUpdateException("회원 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public void updateMemberRelationship(MemberRelationship mr) {
        try {
            MemberRelationship beforeMemberRelationship = memberRelationshipJpaRepository.findByFromMember_MemberIdAndToMember_MemberId(
                    mr.getFromMember().getMemberId(),
                    mr.getToMember().getMemberId()
            ).orElseThrow(() -> new MemberNotFoundException("MemberRelationship not found"));
            if (mr.getRelationshipType() != null) {
                beforeMemberRelationship.setRelationshipType(mr.getRelationshipType());
            }
            if (mr.getActivated() != null) {
                beforeMemberRelationship.setActivated(mr.getActivated());
            }
            if (mr.getFromMember() != null) {
                beforeMemberRelationship.setFromMember(mr.getFromMember());
            }
            if (mr.getToMember() != null) {
                beforeMemberRelationship.setToMember(mr.getToMember());
            }
            memberRelationshipJpaRepository.save(beforeMemberRelationship);
            log.info("회원 관계 업데이트 성공 : {}", mr);
        } catch (DataAccessException e) {
            log.error("회원 관계 업데이트 중 오류 발생", e);
            throw new MemberRelationshipUpdateException("회원 관계 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {
        try {
            memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                    member -> {
                        member.setPwd(newPassword);
                        memberJpaRepository.save(member);
                        log.info("회원 비밀번호 업데이트 성공! :  {}", newPassword);
                    });
        } catch (DataAccessException e) {
            log.error("회원 비밀번호 업데이트 중 오류 발생", e);
            throw new MemberUpdateException("회원 비밀번호 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public void updatePhoneNumber(String memberId, String newPhoneNumber) {
        try {
            memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                    member -> {
                        member.setPhoneNumber(newPhoneNumber);
                        memberJpaRepository.save(member);
                        log.info("회원 전화번호 업데이트 성공! :  {}", newPhoneNumber);
                    });
        } catch (DataAccessException e) {
            log.error("회원 전화번호 업데이트 중 오류 발생", e);
            throw new MemberUpdateException("회원 전화번호 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        try {
            memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                    member -> {
                        member.setProfilePhotoUrl(newProfilePhotoUrl);
                        memberJpaRepository.save(member);
                    });
        } catch (DataAccessException e) {
            log.error("회원 프로필 사진 업데이트 중 오류 발생", e);
            throw new MemberUpdateException("회원 프로필 사진 업데이트 중 오류 발생", e);
        }
    }
    @Override
    public void updateMemberProfileIntro(String memberId, String newProfileIntro) {
        try {
            memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                    member -> {
                        member.setProfileIntro(newProfileIntro);
                        memberJpaRepository.save(member);
                    });
        } catch (DataAccessException e) {
            log.error("회원 프로필 소개 업데이트 중 오류 발생", e);
            throw new MemberUpdateException("회원 프로필 소개 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public void deleteMemberRelationship(String fromId, String toId) {
        try {
            memberRelationshipJpaRepository.deleteByFromMember_MemberIdAndToMember_MemberId(fromId, toId);
        } catch (DataAccessException e) {
            log.error("회원 관계 삭제 중 오류 발생", e);
            throw new MemberRelationshipUpdateException("회원 관계 삭제 중 오류 발생", e);
        }
    }

    @Override
    public void deleteMember(String memberId) {
        try {
            memberJpaRepository.deleteById(memberId);
        } catch (DataAccessException e) {
            log.error("회원 삭제 중 오류 발생", e);
            throw new MemberDeletionException("회원 삭제 중 오류 발생", e);
        }
    }

//    @Override
//    public void deleteMemberRelationship(String fromId, String toId, RelationshipType relationshipType) {
//        log.info("회원 관계 삭제 중 : fromId : {},toId : {}, relationshipType : {} ", fromId, toId, relationshipType);
//        memberRelationshipJpaRepository.deleteByToMember_MemberIdAndFromMember_MemberIdAndRelationshipType(toId, fromId, relationshipType);
//        log.info("회원 관계 삭제 성공");
//    }
//
//    @Override
//    public void deleteMember(String memberId) {
//        log.info("회원 삭제 중 : {}", memberId);
//        memberRepository.
//        log.info("회원 삭제 성공 : {}", memberId);
//    }
}
