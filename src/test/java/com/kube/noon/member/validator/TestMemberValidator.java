package com.kube.noon.member.validator;

import com.kube.noon.member.domain.Member;
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

    }

}
