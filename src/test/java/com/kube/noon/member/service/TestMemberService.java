package com.kube.noon.member.service;

import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.AddMemberRelationshipDto;
import com.kube.noon.member.enums.RelationshipType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class TestMemberService {

    @Autowired
    private MemberService memberService;

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
        assertThat(memberService.findMemberById("member_1230")).isNotNull();
    }

    @Test
    @DisplayName("회원 관계 추가 테스트")
    void addMemberRelationship() {
        memberService.addMemberRelationship(AddMemberRelationshipDto.builder()
                .fromId("member_1")
                .toId("member_2")
                .relationshipType(RelationshipType.FOLLOW)
                .build());
        log.info("회원 관계 추가 테스트");
    }


}
