package com.kube.noon.member.service;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.MemberRelationshipDto;
import com.kube.noon.member.dto.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.MemberSearchCriteriaDto;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class TestMemberService {


    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    @Autowired
    private MemberService memberService;
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 추가 테스트")
    void addMember() {
        memberService.addMember(AddMemberDto.builder()
                        .memberId("member_1230")
                .nickname("test4586123")
                .pwd(null)
                .phoneNumber("010-1234-5678")
                .socialSignUp(true)
                .build());
        log.info("회원 추가 테스트");
        assertThat(memberService.findMemberByMemberId("member_1230")).isNotNull();
        memberRepository.deleteMember("member_1230");
    }

    @Test
    @DisplayName("회원 관계 추가 테스트")
    void addMemberRelationship() {
        memberService.addMemberRelationship(MemberRelationshipDto.builder()
                .fromId("member_1")
                .toId("member_2")
                .relationshipType(RelationshipType.FOLLOW)
                .build());
        log.info("회원 관계 추가 테스트");
    }

    @Test
    @DisplayName("회원 찾기 테스트")
    void findMemberByMemberId() {
        log.info("회원 찾기 테스트 :: member_1 :: ");
        log.info(ANSI_RED + memberService.findMemberByMemberId("member_1") + ANSI_RESET);
        assertThat(memberService.findMemberByMemberId("member_1")).isNotNull();
    }
    @Test
    @DisplayName("회원 프로필  찾기 테스트")
    void findMemberProfileById() {
        log.info("회원 프로필 찾기 테스트");
        assertThat(memberService.findMemberProfileById("member_1")).isNotNull();
    }

    @Test
    @DisplayName("회원 닉네임으로 찾기 테스트")
    void findMemberByNickname() {
        log.info("회원 닉네임으로 찾기 테스트");
        assertThat(memberService.findMemberByNickname("nickname_1")).isNotNull();
    }

    @Test
    @DisplayName("회원 리스트 찾기 테스트")
    void findMemberListByCriteria() {
        log.info("회원 리스트 찾기 테스트"+ memberService.findMemberListByCriteria(
                MemberSearchCriteriaDto
                        .builder()
                        .nickname("nickname")
                        .signedOff(false)
                        .build()));
        assertThat(memberService.findMemberListByCriteria(
                MemberSearchCriteriaDto
                .builder()
                        .nickname("nickname")
                        .signedOff(false)
                        .build()
        )).isNotNull();


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

    private @NotNull MemberRelationship getMemberRelationship(String fromId, String toId,RelationshipType relationshipType) {
        MemberRelationship mr = new MemberRelationship();
        mr.setRelationshipType(relationshipType);//FOLLOW
        mr.setFromMember(memberService.findMemberByMemberId(fromId).get());//"member_1"
        mr.setToMember(memberService.findMemberByMemberId(toId).get());//"member_99"
        mr.setActivated(true);
        return mr;
    }

    @Builder
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
