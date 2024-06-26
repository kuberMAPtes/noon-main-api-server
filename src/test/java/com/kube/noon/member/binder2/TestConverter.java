package com.kube.noon.member.binder2;

import com.kube.noon.common.PublicRange;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.AddMemberDto;
import com.kube.noon.member.dto.member.MemberProfileDto;
import com.kube.noon.member.dto.member.UpdateMemberDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TestConverter {

    @Test
    @DisplayName("예제")
    public void exampleUsage() {
        AddMemberDto addMemberDto = new AddMemberDto();
        addMemberDto.setPhoneNumber("016-1234-5678");
        addMemberDto.setMemberId("testId1234");
        addMemberDto.setNickname("testNickname1");
        addMemberDto.setPwd("testPassword1");
        Member member = DtoEntityBinder.INSTANCE.toEntity(addMemberDto);

        System.out.println("멤버");
        System.out.println(member);

        System.out.println("Dto");
        AddMemberDto addMemberDto1 =DtoEntityBinder.INSTANCE.toDto(member, AddMemberDto.class);
        System.out.println(addMemberDto1);
        //------------------------------------------------------------------------------------
        MemberProfileDto memberProfileDto = new MemberProfileDto();
        memberProfileDto.setMemberId("testI3d1234");
        memberProfileDto.setNickname("testNickna23me1");
        memberProfileDto.setProfilePhotoUrl("testUrl");
        memberProfileDto.setProfileIntro("안녕하시렵니까");
        memberProfileDto.setFeedDtoList(null);
        memberProfileDto.setDajungScore(15);
        Member member1 = DtoEntityBinder.INSTANCE.toEntity(memberProfileDto);
        System.out.println("멤버");
        System.out.println(member1);

        System.out.println("Dto");
        MemberProfileDto memberProfileDto1 =DtoEntityBinder.INSTANCE.toDto(member1, MemberProfileDto.class);

        System.out.println(memberProfileDto1);
        //------------------------------------------------------------------------------------
        UpdateMemberDto updateMemberDto = new UpdateMemberDto();
        updateMemberDto.setMemberId("testId1234");
        updateMemberDto.setNickname("testNickname1");
        updateMemberDto.setAllFeedPublicRange(PublicRange.PRIVATE);
        Member member2 = DtoEntityBinder.INSTANCE.toEntity(updateMemberDto);
        System.out.println("멤버");
        System.out.println(member2);

        System.out.println("Dto");
        UpdateMemberDto updateMemberDto1 = DtoEntityBinder.INSTANCE.toDto(member2, UpdateMemberDto.class);
        System.out.println(updateMemberDto1);

        DtoEntityBinder.INSTANCE.toEntity(updateMemberDto1);

    }


}
