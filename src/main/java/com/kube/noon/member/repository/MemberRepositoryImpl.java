package com.kube.noon.member.repository;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRelationshipJpaRepository memberRelationshipJpaRepository;
//    private final PasswordEncoder passwordEncoder;

    @Override
    public void addMember(Member member) {
        log.info("회원 추가 중 : {}", member);
        memberJpaRepository.save(member);
        log.info("회원 추가 성공 : {}", member);
    }

    @Override
    public void addMemberRelationship(MemberRelationship memberRelationship) {
        log.info("회원 관계 추가 중 : {}", memberRelationship);
        memberRelationshipJpaRepository.save(memberRelationship);
        log.info("회원 관계 추가 성공 : {}", memberRelationship);
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        log.info("회원 찾는 중 ID: {}", memberId);
        Optional<Member> op = memberJpaRepository.findMemberByMemberId(memberId);
        op.ifPresentOrElse(
                member -> log.info("ID로 회원 찾기 성공 : {}", member.getNickname()),
                () -> log.info("회원 찾기 실패 : 해당 ID의 회원이 없음")
        );
        return op;
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        Optional<Member> om = memberJpaRepository.findMemberByNickname(nickname);
        om.ifPresentOrElse(
                member -> log.info("닉네임으로 회원 찾기 성공 : {}", member.getNickname()),
                () -> log.info("회원 찾기 실패 : 해당 닉네임의 회원이 없음")
        );
        return om;
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
    public Optional<MemberRelationship> findMemberRelationship(String fromId, String toId, RelationshipType relationshipType) {
        Optional<MemberRelationship> omr = memberRelationshipJpaRepository.
                findByToMember_MemberIdAndFromMember_MemberIdAndRelationshipType(fromId, toId, relationshipType);
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
        log.info("회원 업데이트 중");
        memberJpaRepository.save(member);
        log.info("회원 업데이트 성공");
    }

    @Override
    public void updatePassword(String memberId, String newPassword) {
        memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                member -> {
//                    String encryptedPassword = passwordEncoder.encode(newPassword);
//                    member.setPwd(encryptedPassword);
                    member.setPwd(newPassword);
                    memberJpaRepository.save(member);
                });
    }

    @Override
    public void updatePhoneNumber(String memberId, String newPhoneNumber) {
        log.info("회원 전화번호 업데이트 중 :  {}", memberId);
        memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                member -> {
                    member.setPhoneNumber(newPhoneNumber);
                    memberJpaRepository.save(member);
                    log.info("회원 전화번호 업데이트 성공! :  {}", memberId);
                });
    }

    @Override
    public void updateMemberProfilePhoto(String memberId, String newProfilePhotoUrl) {
        log.info("회원 프로필 사진 업데이트 중 : {}", memberId);
        memberJpaRepository.findMemberByMemberId(memberId).ifPresent(
                member -> {
                    member.setProfilePhotoUrl(newProfilePhotoUrl);
                    memberJpaRepository.save(member);
                });
    }

    @Override
    public void deleteMemberRelationship(String fromId, String toId, RelationshipType relationshipType) {
        log.info("회원 관계 삭제 중 : fromId : {},toId : {}, relationshipType : {} ", fromId, toId, relationshipType);
        memberRelationshipJpaRepository.deleteByToMember_MemberIdAndFromMember_MemberIdAndRelationshipType(toId, fromId, relationshipType);
        log.info("회원 관계 삭제 성공");
    }

    @Override
    public void deleteMember(String memberId) {
        log.info("회원 삭제 중 : {}", memberId);
        memberJpaRepository.deleteById(memberId);
        log.info("회원 삭제 성공 : {}", memberId);
    }
}
