package com.kube.noon.member.repository;


import com.kube.noon.common.PublicRange;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.enums.Role;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TestMemberRepositoryImpl {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 추가 테스트")
    void addMember() {
        String dateString = "0001-01-01 01:01:01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Member newMember = getMember(dateString, formatter);
//
//        System.out.println(passwordEncoder.encode("newPassword"));
//        newMember.setPwd(passwordEncoder.encode("newPassword"));

        memberRepository.addMember(newMember);

        Optional<Member> foundMember = memberRepository.findMemberById("member_99999");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNickname()).isEqualTo("newMember");
    }

    private static @NotNull Member getMember(String dateString, DateTimeFormatter formatter) {
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
}
