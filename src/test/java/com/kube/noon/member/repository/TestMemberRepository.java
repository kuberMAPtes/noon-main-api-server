package com.kube.noon.member.repository;


import com.kube.noon.common.PublicRange;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.memberRelationship.FindMemberRelationshipListByCriteriaDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.enums.Role;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.kube.noon.member.enums.RelationshipType.BLOCK;
import static com.kube.noon.member.enums.RelationshipType.FOLLOW;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class TestMemberRepository {

    @Autowired
    private MemberRepository memberRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 추가 테스트")
    void addMember() {
        Member m = getMember();
//        System.out.println(passwordEncoder.encode("newPassword"));
//        newMember.setPwd(passwordEncoder.encode("newPassword"));

        memberRepository.addMember(m);

        Optional<Member> foundMember = memberRepository.findMemberById("member_99999");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNickname()).isEqualTo("newMember");
    }

    /**
     * 회원 관계 추가 테스트
     * 1. 중복체크를 DB 에서 전혀 못함. 중복된 값을 허용함. Service 레이어에서 중복체크를 해야 함.
     */
    @Test
    @DisplayName("회원 관계 추가 테스트")
    void addMemberRelationship() {

        MemberRelationship mr1 = getMemberRelationship(FOLLOW, "member_7", "member_1");
        MemberRelationship mr2 = getMemberRelationship(FOLLOW, "member_1", "member_7");
        MemberRelationship mr3 = getMemberRelationship(BLOCK, "member_8", "member_1");
        MemberRelationship mr4 = getMemberRelationship(BLOCK, "member_1", "member_8");
        MemberRelationshipSearchCriteriaDto mrsc = getMemberRelationshipSearchCriteriaDto("member_1", true, true, false, false);
        memberRepository.addMemberRelationship(mr1);
        memberRepository.addMemberRelationship(mr2);
        memberRepository.addMemberRelationship(mr3);
        memberRepository.addMemberRelationship(mr4);
        FindMemberRelationshipListByCriteriaDto findMemberRelationshipListByCriteriaDto = memberRepository.findMemberRelationshipListByCriteria(mrsc,1,5);

        Page<MemberRelationship> foundMemberRelationship = findMemberRelationshipListByCriteriaDto.getMemberRelationshipPage();

        for (MemberRelationship mr : foundMemberRelationship) {
            log.info("member_1의 FromId 리스트 출력 : {}", mr.getFromMember().getMemberId());
        }
        for (MemberRelationship mr : foundMemberRelationship) {
            log.info("member_1의 ToId 리스트 출력 : {}", mr.getToMember().getMemberId());
//            System.out.println("가즈아아아\n"+ mr.getToMember().getNickname());
//            System.out.println(mr.getFromMember().getNickname());
            System.out.println("가즈아아아");
            System.out.println(mr.getToMember().toString());
        }
        memberRepository.deleteMemberRelationship("member_1", "member_7");
        memberRepository.deleteMemberRelationship("member_7", "member_1");
        memberRepository.deleteMemberRelationship("member_1", "member_8");
        memberRepository.deleteMemberRelationship("member_8", "member_1");
    }

    @Test
    @DisplayName("회원ID로 찾기 테스트")
    void findMemberById() {

        Optional<Member> foundMember = memberRepository.findMemberById("member_1");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNickname()).isEqualTo("nickname_1");

        memberRepository.findMemberById("member_100").ifPresent(
                member -> {
                    System.out.println(member);
                    System.out.println(member.getPhoneNumber().equals("010-0000-0000"));
                }
        );

    }

    @Test
    @DisplayName("회원 닉네임으로 찾기 테스트")
    void findMemberByNickname() {
        Optional<Member> foundMember = memberRepository.findMemberByNickname("nickname_1");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNickname()).isEqualTo("nickname_1");
    }

    @Test
    void findMemberByNickname_paging() {
        for (int i = 1; i <= 25; i++) {
            Member saveMember = new Member();
            saveMember.setMemberId("sample-" + i);
            saveMember.setNickname("sample-nickname-" + i);
            saveMember.setPhoneNumber("010591243" + (i < 10 ? "0" + i : i));
            saveMember.setPwd("samplepwd");
            this.memberRepository.addMember(saveMember);
        }

        Page<Member> result1 = this.memberRepository.findMemberByNickname("sample-nickname", "none", 1);
        Page<Member> result2 = this.memberRepository.findMemberByNickname("sample-nickname", "none", 3);
        Page<Member> result3 = this.memberRepository.findMemberByNickname("sample-nickname", "none", 4);

        assertThat(result1.getTotalPages()).isEqualTo(3);
        assertThat(result1.getTotalElements()).isEqualTo(25);
        assertThat(result2.getTotalPages()).isEqualTo(3);
        assertThat(result2.getTotalElements()).isEqualTo(25);
        assertThat(result3.getTotalPages()).isEqualTo(3);
        assertThat(result3.getTotalElements()).isEqualTo(25);

        assertThat(result1.getContent().size()).isEqualTo(10);
        assertThat(result2.getContent().size()).isEqualTo(5);
        assertThat(result3.getContent().isEmpty()).isTrue();
    }

    @Test
    void findMemberByNickname_paging_negativePage() {
        for (int i = 1; i <= 25; i++) {
            Member saveMember = new Member();
            saveMember.setMemberId("sample-" + i);
            saveMember.setNickname("sample-nickname-" + i);
            saveMember.setPhoneNumber("010591243" + (i < 10 ? "0" + i : i));
            saveMember.setPwd("samplepwd");
            this.memberRepository.addMember(saveMember);
        }

        Page<Member> result1 = this.memberRepository.findMemberByNickname("sample-nickname", "none", 0);
        Page<Member> result2 = this.memberRepository.findMemberByNickname("sample-nickname", "none", -1);
        assertThat(result1.getContent().size()).isZero();
        assertThat(result2.getContent().size()).isZero();
    }

    @Test
    @DisplayName("회원 리스트 조회 테스트")
    void findMemberListByCriteria() {
        Page<Member> foundMemberList = memberRepository.findMemberListByCriteria(
                getMemberSearchCriteriaDto("member_1", null, null, null, null, false),5,3);
        System.out.println("리스트조회" + foundMemberList);
        System.out.println(foundMemberList.getContent());
    }

    @Test
    @DisplayName("회원 관계 리스트 조회 테스트")
    void findMemberRelationshipListByCriteria() {
        MemberRelationshipSearchCriteriaDto mrsc = getMemberRelationshipSearchCriteriaDto("member_1", true, true, false, false);
        FindMemberRelationshipListByCriteriaDto findMemberRelationshipListByCriteriaDto = memberRepository.findMemberRelationshipListByCriteria(mrsc,3,5);
        Page<MemberRelationship> foundMemberRelationship = findMemberRelationshipListByCriteriaDto.getMemberRelationshipPage();
        log.info("member_1의 FromId 리스트 출력 : {}", foundMemberRelationship.toList());

    }

    @Test
    @DisplayName("회원 업데이트 테스트")
    void updateMember() {

        memberRepository.updateMember(
                memberRepository.findMemberById("member_1")
                        .map(
                                member -> {
                                    member.setProfilePhotoUrl("https://www.naver.com");
                                    return member;
                                }).orElseThrow());

        assertThat(memberRepository.findMemberById("member_1").get().getProfilePhotoUrl())
                .isEqualTo("https://www.naver.com");

        memberRepository.updateMember(
                memberRepository.findMemberById("member_1")
                        .map(
                                member -> {
                                    member.setProfilePhotoUrl(null);
                                    return member;
                                }).orElseThrow());

        assertThat(memberRepository.findMemberById("member_1").get().getProfilePhotoUrl())
                .isNull();
    }
    @Test
    @DisplayName("회원 업데이트 테스트 : null 값이 들어왔을 때")
    void updateMember2(){
        //널을 넣어도 널이 안되어야 함.
        //업데이트한 건 업데이트 되어야함
        Member member = memberRepository.findMemberById("member_1").orElseThrow();
        Member rollbackMember = member;
        member.setMemberProfilePublicRange(PublicRange.PRIVATE);
        member.setAllFeedPublicRange(PublicRange.PRIVATE);
        member.setBuildingSubscriptionPublicRange(PublicRange.PRIVATE);
        member.setReceivingAllNotificationAllowed(true);
        member.setNickname("새로운 닉네임");
        memberRepository.updateMember(member);
        assertThat(memberRepository.findMemberById("member_1").get().getNickname()).isEqualTo("새로운 닉네임");
        assertThat(memberRepository.findMemberById("member_1").get().getAllFeedPublicRange()).isEqualTo(PublicRange.PRIVATE);
        assertThat(memberRepository.findMemberById("member_1").get().getBuildingSubscriptionPublicRange()).isEqualTo(PublicRange.PRIVATE);
        assertThat(memberRepository.findMemberById("member_1").get().getMemberProfilePublicRange()).isEqualTo(PublicRange.PRIVATE);
        assertThat(memberRepository.findMemberById("member_1").get().getReceivingAllNotificationAllowed()).isTrue();
        memberRepository.updateMember(rollbackMember);
    }

    @Test
    @DisplayName("회원 비밀번호 업데이트 테스트")
    void updatePassword() {
        memberRepository.updatePassword("member_1", "newPassword");

        Optional<Member> foundMember = memberRepository.findMemberById("member_1");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPwd()).isEqualTo("newPassword");
    }

    @Test
    @DisplayName("회원 전화번호 업데이트 테스트")
    void updatePhoneNumber() {
        memberRepository.updatePhoneNumber("member_1", "010-1234-5678");

        Optional<Member> foundMember = memberRepository.findMemberById("member_1");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPhoneNumber()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("회원 프로필 사진 업데이트 테스트")
    void updateProfilePhotoUrl() {
        memberRepository.updateMemberProfilePhoto("member_1", "https://newprofile.photo/url");

        Optional<Member> foundMember = memberRepository.findMemberById("member_1");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getProfilePhotoUrl()).isEqualTo("https://newprofile.photo/url");

    }

    @Test
    @DisplayName("회원 관계 삭제 테스트")
    void deleteMemberRelationship() {
        MemberRelationship mr = memberRepository.findMemberRelationship("member_1","member_3", FOLLOW).orElseThrow();
        assertThat(mr.getActivated()).isTrue();
        mr.setActivated(false);
        memberRepository.updateMemberRelationship(mr);
        assertThat(memberRepository.findMemberRelationship("member_1","member_3", FOLLOW).get().getActivated()).isFalse();
        memberRepository.updateMemberRelationship(MemberRelationship.builder().fromMember(memberRepository.findMemberById("member_1").get()).toMember(memberRepository.findMemberById("member_3").get()).activated(true).relationshipType(FOLLOW).build());
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteMember() {
        Member member = memberRepository.findMemberById("member_1").orElseThrow();
        assertThat(member.getSignedOff()).isFalse();
        member.setSignedOff(true);
        memberRepository.updateMember(member);
        Optional<Member> foundMember = memberRepository.findMemberById("member_1");
        assertThat(foundMember.get().getSignedOff()).isTrue();
        memberRepository.updateMember(Member.builder().memberId("member_1").signedOff(false).build());
    }


    private @NotNull Member getMember() {
        String dateString = "0001-01-01 01:01:01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        Member newMember = new Member();
        newMember.setMemberId("member_99999");
        newMember.setMemberRole(Role.MEMBER);
        newMember.setNickname("newMember");
        newMember.setPwd("12341234");
        newMember.setPhoneNumber("010-8765-3421");
        newMember.setUnlockTime(dateTime);
        newMember.setProfilePhotoUrl("https://www.naver.com");
        newMember.setProfileIntro("프로필 소개");
        newMember.setDajungScore(0);
        newMember.setSignedOff(false);
        newMember.setBuildingSubscriptionPublicRange(PublicRange.PUBLIC);
        newMember.setAllFeedPublicRange(PublicRange.PUBLIC);
        newMember.setMemberProfilePublicRange(PublicRange.PUBLIC);
        newMember.setReceivingAllNotificationAllowed(true);
        return newMember;
    }

    private @NotNull MemberSearchCriteriaDto getMemberSearchCriteriaDto(String memberId, String nickname, LocalDateTime startTime, LocalDateTime endTime, String phoneNumber, Boolean signedOff) {
        return MemberSearchCriteriaDto.builder()
                .memberId(memberId)
                .nickname(nickname)
                .startTime(startTime)
                .endTime(endTime)
                .phoneNumber(phoneNumber)
                .signedOff(signedOff)
                .build();
    }

    private @NotNull MemberRelationship getMemberRelationship(RelationshipType relationshipType, String fromId, String toId) {
        MemberRelationship mr = new MemberRelationship();
        mr.setRelationshipType(relationshipType);//FOLLOW
        mr.setFromMember(memberRepository.findMemberById(fromId).get());//"member_99"
        mr.setToMember(memberRepository.findMemberById(toId).get());//"member_1"
        mr.setActivated(true);
        return mr;
    }

    private @NotNull MemberRelationshipSearchCriteriaDto getMemberRelationshipSearchCriteriaDto(String memberId, boolean following, boolean follower, boolean blocking, boolean blocker) {

        return MemberRelationshipSearchCriteriaDto.builder().
                memberId(memberId)
                .following(following)
                .follower(follower)
                .blocking(blocking)
                .blocker(blocker)
                .build();
    }
}
