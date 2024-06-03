package com.kube.noon.member.repository;


import com.kube.noon.common.PublicRange;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.enums.Role;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.kube.noon.member.enums.RelationshipType.BLOCK;
import static com.kube.noon.member.enums.RelationshipType.FOLLOW;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class TestMemberRepositoryImpl {

    @Autowired
    private MemberRepository memberRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    //    @Test
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
//    @Test
    @DisplayName("회원 관계 추가 테스트")
    void addMemberRelationship() {
        MemberRelationship mr1 = getMemberRelationship(FOLLOW, "member_99", "member_1");
        MemberRelationship mr2 = getMemberRelationship(FOLLOW, "member_1", "member_99");
        MemberRelationship mr3 = getMemberRelationship(BLOCK, "member_99", "member_1");
        MemberRelationship mr4 = getMemberRelationship(BLOCK, "member_1", "member_99");
        MemberRelationshipSearchCriteriaDto mrsc = getMemberRelationshipSearchCriteriaDto("member_1", true, true, false, false);
        memberRepository.addMemberRelationship(mr1);
        memberRepository.addMemberRelationship(mr2);
        memberRepository.addMemberRelationship(mr3);
        memberRepository.addMemberRelationship(mr4);
        List<MemberRelationship> foundMemberRelationship = memberRepository.findMemberRelationshipListByCriteria(mrsc);


        for (MemberRelationship mr : foundMemberRelationship) {
            log.info("member_1의 FromId 리스트 출력 : {}", mr.getFromId());
        }
        for (MemberRelationship mr : foundMemberRelationship) {
            log.info("member_1의 ToId 리스트 출력 : {}", mr.getToId());
//            System.out.println("가즈아아아\n"+ mr.getToMember().getNickname());
//            System.out.println(mr.getFromMember().getNickname());
            System.out.println("가즈아아아");
            System.out.println(mr.getToMember().toString());
        }
    }

    @Test
    @DisplayName("회원ID로 찾기 테스트")
    void findMemberById() {

        Optional<Member> foundMember = memberRepository.findMemberById("member_1");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNickname()).isEqualTo("nickname_1");

    }

    @Test
    @DisplayName("회원 닉네임으로 찾기 테스트")
    void findMemberByNickname() {
        Optional<Member> foundMember = memberRepository.findMemberByNickname("nickname_1");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNickname()).isEqualTo("nickname_1");
    }

    @Test
    @DisplayName("회원 리스트 조회 테스트")
    void findMemberListByCriteria() {
        List<Member> foundMemberList = memberRepository.findMemberListByCriteria(
                getMemberSearchCriteriaDto("member_1", "nickname_1", null, null, null, false));
        System.out.println("리스트조회" + foundMemberList);
        assertThat(foundMemberList.get(0).getNickname()).isEqualTo("nickname_1");
    }

    @Test
    @DisplayName("회원 관계 리스트 조회 테스트")
    void findMemberRelationshipListByCriteria() {
        MemberRelationshipSearchCriteriaDto mrsc = getMemberRelationshipSearchCriteriaDto("member_1", true, true, false, false);
        List<MemberRelationship> foundMemberRelationship = memberRepository.findMemberRelationshipListByCriteria(mrsc);
        log.info("member_1의 FromId 리스트 출력 : {}", foundMemberRelationship);

    }

    @Test
    @DisplayName("회원 업데이트 테스트")
    void updateMember() {

        memberRepository.updateMember(getMember());
        assertThat(memberRepository.findMemberById("member_99999").get().getProfilePhotoUrl())
                .isEqualTo("https://www.naver.com");

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
        MemberRelationship mr = getMemberRelationship(FOLLOW, "member_99", "member_1");
        memberRepository.deleteMemberRelationship("member_1", "member_99", FOLLOW);
        MemberRelationshipSearchCriteriaDto mrsc = getMemberRelationshipSearchCriteriaDto("member_99", true, false, false, false);
        List<MemberRelationship> foundMemberRelationship = memberRepository.findMemberRelationshipListByCriteria(mrsc);
        log.info("member_1의 FromId 리스트 출력 : {}", foundMemberRelationship);
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteMember() {
        memberRepository.deleteMember("member_100");
        Optional<Member> foundMember = memberRepository.findMemberById("member_100");
        assertThat(foundMember).isEmpty();
    }


    private static @NotNull Member getMember() {
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

    private static @NotNull MemberSearchCriteriaDto getMemberSearchCriteriaDto(String memberId, String nickname, LocalDateTime startTime, LocalDateTime endTime, String phoneNumber, Boolean signedOff) {
        return MemberSearchCriteriaDto.builder()
                .memberId(memberId)
                .nickname(nickname)
                .startTime(startTime)
                .endTime(endTime)
                .phoneNumber(phoneNumber)
                .signedOff(signedOff)
                .build();
    }

    private static @NotNull MemberRelationship getMemberRelationship(RelationshipType relationshipType, String fromId, String toId) {
        MemberRelationship mr = new MemberRelationship();
        mr.setRelationshipType(relationshipType);//FOLLOW
        mr.setFromId(fromId);//"member_1"
        mr.setToId(toId);//"member_99"
        mr.setActivated(true);
        return mr;
    }

    private static @NotNull MemberRelationshipSearchCriteriaDto getMemberRelationshipSearchCriteriaDto(String memberId, boolean following, boolean follower, boolean blocking, boolean blocker) {

        return MemberRelationshipSearchCriteriaDto.builder().
                memberId(memberId)
                .following(following)
                .follower(follower)
                .blocking(blocking)
                .blocker(blocker)
                .build();
    }
}
