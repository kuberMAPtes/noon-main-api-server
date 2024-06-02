package com.kube.noon.member.repository;

import com.kube.noon.member.binder.MemberBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.AddMemberRelationshipDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.kube.noon.common.PublicRange.PUBLIC;
import static com.kube.noon.member.enums.Role.MEMBER;

@Slf4j
@SpringBootTest
public class TestMemberRepository {

    @Autowired
    MemberRepository memberRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("회원 추가 테스트")
    void addMember() {
        AddMemberDto addMemberDto = AddMemberDto.builder()
                .memberId("testId")
                .memberRole(MEMBER)
                .pwd("testPwd")
                .nickname("testNickname")
                .phoneNumber("010-1234-5678")
                .unlockTime(LocalDateTime.parse("2021-08-01 00:00:00", formatter))
                .profilePhotoUrl("testUrl")
                .profileIntro("testIntro")
                .dajungScore(0)
                .signedOff(false)
                .buildingSubscriptionPublicRange(PUBLIC)
                .allFeedPublicRange(PUBLIC)
                .memberProfilePublicRange(PUBLIC)
                .receivingAllNotificationAllowed(PUBLIC)
                .build();
        Member member = MemberBinder.INSTANCE.AddMemberDtotoMember(addMemberDto);
        System.out.println("""
                Member객체 :: """+member);
        System.out.println(memberRepository.findMemberById("testId"));

        memberRepository.addMember(member);
        System.out.println(memberRepository.findMemberById("testId"));
    }

    @Test
    @DisplayName("회원 관계 추가 테스트")
    void addMemberRelationship() {
    }
    @Test
    @DisplayName("회원 정보 조회 테스트")
    void findMemberById() {
    }
    @Test
    @DisplayName("회원 프로필 조회 테스트")
    void findMemberProfileById() {
    }
    @Test
    @DisplayName("회원 닉네임 조회 테스트")
    void findMemberByNickname() {
    }
    @Test
    @DisplayName("회원 리스트 조회 테스트")
    void findMemberList() {
    }
    @Test
    @DisplayName("회원 관계 리스트 조회 테스트")
    void findMemberRelationshipList() {
    }
    @Test
    @DisplayName("회원 정보 수정 테스트")
    void updateMember() {
    }
    @Test
    @DisplayName("비밀번호 수정 테스트")
    void updatePassword() {
    }
    @Test
    @DisplayName("전화번호 수정 테스트")
    void updatePhoneNumber() {
    }
    @Test
    @DisplayName("프로필 사진 수정 테스트")
    void updateMemberProfilePhoto() {
    }
    @Test
    @DisplayName("회원 관계 삭제 테스트")
    void deleteMemberRelationship() {
    }
    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteMember() {
    }


}