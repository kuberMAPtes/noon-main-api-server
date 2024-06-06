package com.kube.noon.member.service;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.binder.MemberBinder;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;
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
import java.util.concurrent.atomic.AtomicReference;

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
        assertThat(memberService.findMemberById("member_1230")).isNotNull();
        memberRepository.deleteMember("member_1230");
    }

    @Test
    @DisplayName("회원 관계 추가 테스트")
    void addMemberRelationship() {
        memberService.addMemberRelationship(MemberRelationshipDto.builder()
                .fromId("member_1")
                .toId("member_10")
                .relationshipType(RelationshipType.FOLLOW)
                .build());
        log.info("회원 관계 추가 테스트");
        memberRepository.deleteMemberRelationship("member_1","member_10");
    }

    @Test
    @DisplayName("회원 찾기 테스트")
    void findMemberById() {
        log.info("회원 찾기 테스트 :: member_1 :: ");
        log.info(ANSI_RED + memberService.findMemberById("member_1") + ANSI_RESET);
        assertThat(memberService.findMemberById("member_1")).isNotNull();
    }
    @Test
    @DisplayName("회원 프로필  찾기 테스트")
    void findMemberProfileById() {
        log.info("회원 프로필 찾기 테스트!@#!@");
        assertThat(memberService.findMemberProfileById("member_1")).isNotNull();
    }

    @Test
    @DisplayName("회원 닉네임으로 찾기 테스트")
    void findMemberByNickname() {
        log.info("회원 닉네임으로 찾기 테스트");
        System.out.println("@#$!@#$" + memberService.findMemberByNickname("nickname_1"));
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

    @Test
    @DisplayName("회원 관계 리스트 찾기 테스트")
    void findMemberRelationshipListByCriteria(){
        MemberRelationshipSearchCriteriaDto mrsc= MemberRelationshipSearchCriteriaDto
                .builder()
                .memberId("member_1")
                .following(true)
                .follower(true)
                .blocking(true)
                .blocker(true)
                .build();
        log.info("회원 관계 리스트 찾기 테스트");
        log.info(memberService.findMemberRelationshipListByCriteria(mrsc).toString());
    }

    @Test
    @DisplayName("회원 업데이트 테스트")
    void updateMember() {
        //유저 받아서 속성 바꾸고 업데이트 해봐 업데이트 하고 바뀌었는지 체크
        AtomicReference<UpdateMemberDto> dto2 = new AtomicReference<>();

        memberService.findMemberById("member_1").map(
                member -> {
                    member.setNickname("바뀐 닉네임");
                    member.setDajungScore(100);
                    member.setProfileIntro("바뀐 프로필 소개");
                    dto2.set(MemberBinder.INSTANCE.toDto(member, UpdateMemberDto.class));

                    return MemberBinder.INSTANCE.toDto(member, UpdateMemberDto.class);
                }
        ).ifPresent(dto -> memberService.updateMember(dto));

        assertThat(memberService.findMemberById("member_1").orElseThrow().getNickname()).isEqualTo("바뀐 닉네임");
        memberService.updateMember(dto2.get());
    }
    @Test
    @DisplayName("비밀번호 업데이트 테스트")
    void updatePassword(){

        AtomicReference<UpdatePasswordDto> dto2 = new AtomicReference<>();

        memberService.findMemberById("member_1").map(
                member -> {
                    member.setPwd("1234");
                    dto2.set(MemberBinder.INSTANCE.toDto(member, UpdatePasswordDto.class));
                    return MemberBinder.INSTANCE.toDto(member, UpdatePasswordDto.class);
                }
        ).ifPresent(dto -> memberService.updatePassword(dto.getMemberId(),dto.getPwd()));

        assertThat(memberService.findMemberById("member_1").orElseThrow().getPwd()).isEqualTo("1234");



        memberService.updateMember(MemberBinder.INSTANCE.toDto(MemberBinder.INSTANCE.toMember(dto2.get()), UpdateMemberDto.class));

    }

    @Test
    @DisplayName("전화번호 업데이트 테스트")
    void updatePhoneNumber(){
        AtomicReference<UpdatePhoneNumberDto> dto2 = new AtomicReference<>();

        memberService.findMemberById("member_1").map(
                member -> {
                    member.setPhoneNumber("010-1234-5678");
                    dto2.set(MemberBinder.INSTANCE.toDto(member, UpdatePhoneNumberDto.class));
                    return MemberBinder.INSTANCE.toDto(member, UpdatePhoneNumberDto.class);
                }
        ).ifPresent(dto -> memberService.updatePhoneNumber(dto.getMemberId(),dto.getPhoneNumber()));

        assertThat(memberService.findMemberById("member_1").orElseThrow().getPhoneNumber()).isEqualTo("010-1234-5678");
        memberService.updateMember(MemberBinder.INSTANCE.toDto(MemberBinder.INSTANCE.toMember(dto2.get()), UpdateMemberDto.class));
    }

    @Test
    @DisplayName("프로필 사진 업데이트 테스트")
    void updateMemberProfilePhoto(){

        AtomicReference<UpdateMemberProfilePhotoUrlDto> dto2 = new AtomicReference<>();

        memberService.findMemberById("member_1").map(
                member -> {
                    member.setProfilePhotoUrl("https://www.naver.com");
                    dto2.set(MemberBinder.INSTANCE.toDto(member, UpdateMemberProfilePhotoUrlDto.class));
                    return MemberBinder.INSTANCE.toDto(member, UpdateMemberProfilePhotoUrlDto.class);
                }
        ).ifPresent(dto -> memberService.updateMemberProfilePhoto(dto.getMemberId(),dto.getProfilePhotoUrl()));

        assertThat(memberService.findMemberById("member_1").orElseThrow().getProfilePhotoUrl()).isEqualTo("https://www.naver.com");
        memberService.updateMember(MemberBinder.INSTANCE.toDto(MemberBinder.INSTANCE.toMember(dto2.get()), UpdateMemberDto.class));
    }

    @Test
    @DisplayName("회원 관계 삭제 테스트")
    void deleteMemberRelationship(){
        memberService.deleteMemberRelationship(MemberRelationshipDto.builder()
                .fromId("member_1")
                .toId("member_2")
                .relationshipType(RelationshipType.FOLLOW)
                .build());

        MemberRelationship ms = memberService.findMemberRelationship("member_1","member_2")
                .orElseThrow();
        assertThat(ms.getActivated()).isFalse();
        memberRepository.updateMemberRelationship(ms);
        log.info("회원 관계 삭제 테스트");
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteMember(){

        memberService.deleteMember("member_1");
        assertThat(memberService.findMemberById("member_1").orElseThrow().getSignedOff()).isTrue();


        com.kube.noon.member.domain.Member member = memberService.findMemberById("member_1").orElseThrow();
        memberRepository.updateMember(member);
    }

    @Test
    @DisplayName("회원 탈퇴 여부 확인 테스트")
    void checkMemberisSignedOff(){}




    private @NotNull com.kube.noon.member.domain.Member getMember() {
        String dateString = "0001-01-01 01:01:01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        com.kube.noon.member.domain.Member newMember = new com.kube.noon.member.domain.Member();
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
        mr.setFromMember(memberService.findMemberById(fromId).get());//"member_1"
        mr.setToMember(memberService.findMemberById(toId).get());//"member_99"
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
