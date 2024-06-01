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

@Slf4j
@SpringBootTest
public class TestMemberRepository {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 추가 테스트")
    void addMember() {
        AddMemberDto addMemberDto = AddMemberDto.builder()
                .memberId("testId")
                .memberRole("USER")
                .pwd("testPwd")
                .nickname("testNickname")
                .phoneNumber("010-1234-5678")
                .unlockTime("2021-08-01 00:00:00")
                .profilePhotoUrl("testUrl")
                .profileIntro("testIntro")
                .dajungScore(0)
                .signedOff(false)
                .buildingSubscriptionPublicRange("PUBLIC")
                .allFeedPublicRange("PUBLIC")
                .memberProfilePublicRange("PUBLIC")
                .receivingAllNotificationAllowed("PUBLIC")
                .build();
        Member member = MemberBinder.INSTANCE.toEntity(addMemberDto);
        log.info("member : {}", member);



        memberRepository.addMember(member);
    }

}