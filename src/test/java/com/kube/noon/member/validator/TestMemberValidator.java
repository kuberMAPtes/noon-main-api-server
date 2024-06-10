package com.kube.noon.member.validator;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.AddMemberRelationshipDto;
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
        AddMemberRelationshipDto addMemberRelationshipDto = new AddMemberRelationshipDto();
        addMemberRelationshipDto.setActivated(true);
        addMemberRelationshipDto.setRelationshipType(RelationshipType.FOLLOW);
        addMemberRelationshipDto.setToId("member_1");
        addMemberRelationshipDto.setFromId("member_59");

        memberService.addMemberRelationship(addMemberRelationshipDto);
        //차단했어
        AddMemberRelationshipDto addMemberRelationshipDto2 = new AddMemberRelationshipDto();
        addMemberRelationshipDto2.setActivated(true);
        addMemberRelationshipDto2.setRelationshipType(RelationshipType.BLOCK);
        addMemberRelationshipDto2.setToId("member_1");
        addMemberRelationshipDto2.setFromId("member_59");
        //팔로우했어
        AddMemberRelationshipDto addMemberRelationshipDto3 = new AddMemberRelationshipDto();

        addMemberRelationshipDto3.setRelationshipType(RelationshipType.FOLLOW);
        addMemberRelationshipDto3.setToId("member_1");
        addMemberRelationshipDto3.setFromId("member_59");
        //팔로우차단했어
    }

}
