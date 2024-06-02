package com.kube.noon.member.repository;


import com.kube.noon.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TestMemberRepositoryImpl {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 추가 테스트")
    void addMember() {
        Member newMember = new Member();
        newMember.setMemberId("newMember");
        newMember.setNickname("newNickname");
        newMember.setPhoneNumber("010-8765-4321");
        newMember.setSignedOff(false);
        newMember.setPwd(passwordEncoder.encode("newPassword"));

        memberRepository.addMember(newMember);

        Optional<Member> foundMember = memberRepository.findMemberById("newMember");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getNickname()).isEqualTo("newNickname");
    }
}
