package com.kube.noon.member.repository;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository에서는 받은 데이터는 1차적으로 서비스로부터 검증된 데이터이다.
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
        member.setMemberRole(Role.MEMBER);
        member.setDajungScore(0);
        member.setSignedOff(false);
        member.setMemberProfilePublicRange(PublicRange.PUBLIC);
        member.setBuildingSubscriptionPublicRange(PublicRange.PUBLIC);
        member.setAllFeedPublicRange(PublicRange.PUBLIC);
        member.setReceivingAllNotificationAllowed(true);
        memberJpaRepository.save(member);

    }

    @Override
    public void addMemberRelationship(MemberRelationship memberRelationship) {

        memberRelationshipJpaRepository.findByFromMember_MemberIdAndToMember_MemberId(
                memberRelationship.getFromMember().getMemberId(),
                memberRelationship.getToMember().getMemberId()
        ).ifPresent((relationship) -> {
            throw new MemberNotFoundException("회원관계가 이미 존재합니다");
        });
        memberRelationship.setActivated(true);
        memberRelationshipJpaRepository.save(memberRelationship);

    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        return memberJpaRepository.findMemberByMemberId(memberId);
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        return memberJpaRepository.findMemberByNickname(nickname);
    }

    @Override
    public Optional<Member> findMemberByPhoneNumber(String phoneNumber) {
        return memberJpaRepository.findMemberByPhoneNumber(phoneNumber);
    }

    @Override
    public List<Member> findMemberListByCriteria(MemberSearchCriteriaDto criteria) {
        List<Member> lm = memberJpaRepository.findMemberListByCriteria(criteria);
        if (lm.isEmpty()) {
            log.info("조건에 맞는 회원이 없음");
        } else {
            for (Member m : lm) {
                log.info("memberId : {} 닉네임 :  {}", m.getMemberId(), m.getNickname());
            }
        }
        return lm;
    }

    @Override
    public Optional<MemberRelationship> findMemberRelationship(String fromId, String toId) {
        Optional<MemberRelationship> omr = memberRelationshipJpaRepository.
                findByFromMember_MemberIdAndToMember_MemberId(
                        fromId,
                        toId);
        omr.ifPresentOrElse(
                memberRelationship -> log.info("회원 관계 찾기 성공 : {}", memberRelationship),
                () -> log.info("회원 관계 찾기 실패 : 해당 관계가 없음")
        );
        return omr;
    }


    @Override
    public List<MemberRelationship> findMemberRelationshipListByCriteria(MemberRelationshipSearchCriteriaDto criteria) {
        List<MemberRelationship> lm = memberRelationshipJpaRepository.findMemberRelationshipListByCriteria(criteria);
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
    }

    @Override
    public void updateMember(Member member) {
//        memberJpaRepository.updateMember(member);
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
        // 엔티티는 영속성 컨텍스트에 의해 관리되므로, 별도의 save 호출이 필요없을 수도 있습니다.
        // 그러나 명시적으로 호출하여 변경사항을 저장하는 것도 좋습니다.
        memberJpaRepository.save(newMember);
        log.info("회원 업데이트 성공");
    }

    /**
     * .
     * mr에 넣은대로 바뀐다
     *
     * @param mr
     */
    public void updateMemberRelationship(MemberRelationship mr) {
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
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {
        memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                member -> {
//                    String encryptedPassword = passwordEncoder.encode(newPassword);
//                    member.setPwd(encryptedPassword);
                    member.setPwd(newPassword);
                    memberJpaRepository.save(member);
                    log.info("회원 비밀번호 업데이트 성공! :  {}", newPassword);
                });
    }

    @Override
    public void updatePhoneNumber(String memberId, String newPhoneNumber) {
        memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                member -> {
                    member.setPhoneNumber(newPhoneNumber);
                    memberJpaRepository.save(member);
                    log.info("회원 전화번호 업데이트 성공! :  {}", newPhoneNumber);
                });
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                member -> {
                    member.setProfilePhotoUrl(newProfilePhotoUrl);
                    memberJpaRepository.save(member);
                });
    }

    @Override
    public void deleteMemberRelationship(String fromId, String toId) {
        memberRelationshipJpaRepository.deleteByFromMember_MemberIdAndToMember_MemberId(fromId, toId);
    }

    @Override
    public void deleteMember(String memberId) {
        memberJpaRepository.deleteById(memberId);
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
