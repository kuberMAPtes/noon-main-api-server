package com.kube.noon.member.validator;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.MemberRelationshipDto;
import com.kube.noon.member.enums.RelationshipType;
import com.kube.noon.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@Slf4j
@SpringBootTest
public class TestMemberValidator {

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("Test Validator")
    public void testValidator() {
        Optional<Member> Omember = memberService.findMemberById("member_1");
        System.out.println("member : " + Omember);

        //팔로우했어.
        MemberRelationshipDto memberRelationshipDto = new MemberRelationshipDto();
        memberRelationshipDto.setActivated(true);
        memberRelationshipDto.setRelationshipType(RelationshipType.FOLLOW);
        memberRelationshipDto.setToId("member_1");
        memberRelationshipDto.setFromId("member_59");

        memberService.addMemberRelationship(memberRelationshipDto);
        //차단했어
        MemberRelationshipDto memberRelationshipDto2 = new MemberRelationshipDto();
        memberRelationshipDto2.setActivated(true);
        memberRelationshipDto2.setRelationshipType(RelationshipType.BLOCK);
        memberRelationshipDto2.setToId("member_1");
        memberRelationshipDto2.setFromId("member_59");
        //팔로우했어
        MemberRelationshipDto memberRelationshipDto3 = new MemberRelationshipDto();

        memberRelationshipDto3.setRelationshipType(RelationshipType.FOLLOW);
        memberRelationshipDto3.setToId("member_1");
        memberRelationshipDto3.setFromId("member_59");
        //팔로우차단했어
    }

}
