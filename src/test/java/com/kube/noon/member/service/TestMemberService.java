package com.kube.noon.member.service;

import com.kube.noon.common.PublicRange;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.member.*;
import com.kube.noon.member.dto.memberRelationship.AddMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.DeleteMemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
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
//@Rollback(false)
public class TestMemberService {


    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 추가 테스트")
    void addMember() {
        memberService.addMember(AddMemberDto.builder()
                        .memberId("member_1230")
                .nickname("test4586123")
                .pwd("hello")
                .phoneNumber("010-1234-5678")
                .socialSignUp(true)
                .build());
        log.info("회원 추가 테스트");
        assertThat(memberService.findMemberById("member_100","member_1230")).isNotNull();
        memberRepository.deleteMember("member_1230");
    }

    @Test
    @DisplayName("회원 관계 추가 테스트")
    void addMemberRelationship() {
        memberService.addMemberRelationship(AddMemberRelationshipDto.builder()
                .fromId("member_1")
                .toId("member_10")
                .relationshipType(RelationshipType.FOLLOW)
                .build());
        log.info("회원 관계 추가 테스트");
        assertThat(memberService.findMemberRelationship("member_1","member_10")).isNotNull();
        log.info(ANSI_GREEN + memberService.findMemberRelationship("member_1","member_10") + ANSI_RESET);
//        memberRepository.deleteMemberRelationship("member_1","member_10");
//        assertThat(memberService.findMemberRelationship("member_1","member_10")).isNull();
    }

    @Test
    @DisplayName("회원 찾기 테스트")
    void findMemberById() {
        log.info("회원 찾기 테스트 :: member_1 :: ");
        log.info(ANSI_RED + memberService.findMemberById("member_100","member_1") + ANSI_RESET);
        assertThat(memberService.findMemberById("member_100","member_1")).isNotNull();
    }
    @Test
    @DisplayName("회원 프로필  찾기 테스트")
    void findMemberProfileById() {
        log.info("회원 프로필 찾기 테스트!@#!@");
        assertThat(memberService.findMemberProfileById("member_100","member_1")).isNotNull();
    }

    @Test
    @DisplayName("회원 닉네임으로 찾기 테스트")
    void findMemberByNickname() {
        log.info("회원 닉네임으로 찾기 테스트");
        System.out.println("@#$!@#$" + memberService.findMemberByNickname("member_100","nickname_5"));
        assertThat(memberService.findMemberByNickname("member_100","nickname_5")).isNotNull();
    }

    @Test
    @DisplayName("회원 리스트 찾기 테스트")
    void findMemberListByCriteria() {
        log.info("회원 리스트 찾기 테스트"+ memberService.findMemberListByCriteria("member_100",
                MemberSearchCriteriaDto
                        .builder()
                        .nickname("nickname")
                        .signedOff(false)
                        .build(),2,5));

        System.out.println(memberService.findMemberListByCriteria("member_100",
                MemberSearchCriteriaDto
                        .builder()
                        .nickname("nickname")
                        .signedOff(false)
                        .build(),2,5
        ));

        assertThat(memberService.findMemberListByCriteria("member_100",
                MemberSearchCriteriaDto
                .builder()
                        .nickname("nickname")
                        .signedOff(false)
                        .build(),2,5
        )).isNotNull();
    }

    @Test
    @DisplayName("회원 관계 리스트 찾기 테스트")
    void findMemberRelationshipListByAdmin(){
        MemberRelationshipSearchCriteriaDto mrsc= MemberRelationshipSearchCriteriaDto
                .builder()
                .memberId("member_1")
                .following(true)
                .follower(true)
                .blocking(true)
                .blocker(true)
                .build();
        log.info("회원 관계 리스트 찾기 테스트");
        log.info(memberService.findMemberRelationshipListByCriteria("member_100",mrsc,3,5).toString());
    }

    @Test
    @DisplayName("회원 업데이트 테스트")
    void updateMember() {
        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .memberId("member_40")
                .nickname("바뀐 닉네임")
        .dajungScore(100)
        .profileIntro("바뀐 프로필 소개")
        .unlockTime(LocalDateTime.now().plusDays(1))
        .build();
        memberService.updateMember(updateMemberDto);
        log.info("회원 업데이트 테스트");
    assertThat(memberService.findMemberById("member_100","member_40").getNickname()).isEqualTo("바뀐 닉네임");
    assertThat(memberService.findMemberById("member_100","member_40").getDajungScore()).isEqualTo(100);
    assertThat(memberService.findMemberById("member_100","member_40").getProfileIntro()).isEqualTo("바뀐 프로필 소개");

    }
    @Test
    @DisplayName("비밀번호 업데이트 테스트")
    void updatePassword(){

        MemberDto memberDto = memberService.findMemberById("member_100","member_1");
        UpdatePasswordDto dto = UpdatePasswordDto.builder()
                .memberId(memberDto.getMemberId())
                .pwd("newPassword")
                .build();
        memberService.updatePassword(dto);
        assertThat(memberService.findMemberById("member_100","member_1").getPwd()).isEqualTo("newPassword");

    }

    @Test
    @DisplayName("전화번호 업데이트 테스트")
    void updatePhoneNumber(){

        MemberDto memberDto = memberService.findMemberById("member_100","member_1");
        UpdatePhoneNumberDto dto = UpdatePhoneNumberDto.builder()
                .memberId(memberDto.getMemberId())
                .phoneNumber("010-1234-5678")
                .build();
        memberService.updatePhoneNumber(dto);
        assertThat(memberService.findMemberById("member_100","member_1").getPhoneNumber()).isEqualTo("010-1234-3917");


    }

    @Test
    @DisplayName("프로필 사진 업데이트 테스트")
    void updateMemberProfilePhotoUrl(){

        MemberDto memberDto = memberService.findMemberById("member_100","member_1");
        String profilePhotoUrl = "data:image/webp;base64,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";
        UpdateMemberProfilePhotoUrlDto dto = UpdateMemberProfilePhotoUrlDto.builder()
                .memberId("member_1")
                .profilePhotoUrl("https://www.naver.com")
                .build();
        memberService.updateMemberProfilePhotoUrl(dto);
        assertThat(memberService.findMemberById("member_100",memberDto.getMemberId()).getProfilePhotoUrl()).isEqualTo(profilePhotoUrl);

    }

    @Test
    @DisplayName("회원 관계 삭제 테스트")
    void deleteMemberRelationship(){
        memberService.deleteMemberRelationship(DeleteMemberRelationshipDto.builder()
                .fromId("member_1")
                .toId("member_2")
                .relationshipType(RelationshipType.FOLLOW)
                .build());

        MemberRelationshipDto ms = memberService.findMemberRelationship("member_1","member_2");
        assertThat(ms.getActivated()).isFalse();


        memberRepository.updateMemberRelationship(DtoEntityBinder.INSTANCE.toEntity(ms));
        log.info("회원 관계 삭제 테스트");
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteMember(){

        memberService.deleteMember("member_1");
        assertThat(memberService.findMemberById("member_100","member_1").getSignedOff()).isTrue();


        Member member = DtoEntityBinder.INSTANCE.toEntity(memberService.findMemberById("member_100","member_1"));
        memberRepository.updateMember(member);
    }

    @Test
    @DisplayName("회원 탈퇴 여부 확인 테스트")
    void checkMemberisSignedOff(){}




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
        mr.setFromMember(DtoEntityBinder.INSTANCE.toEntity(memberService.findMemberById("member_100",fromId)));//"member_1"
        mr.setToMember(DtoEntityBinder.INSTANCE.toEntity(memberService.findMemberById("member_100",toId)));//"member_99"
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
