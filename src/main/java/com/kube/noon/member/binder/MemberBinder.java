package com.kube.noon.member.binder;


import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberBinder {

    MemberBinder INSTANCE = Mappers.getMapper(MemberBinder.class);

    Member AddMemberDtoToMember(AddMemberDto dto);

    MemberRelationship AddMemberRelationshipDtoToMember(AddMemberRelationshipDto dto);

    Member MemberProfileDtoToMember(MemberProfileDto dto);

    Member UpdateMemberDtoToMember(UpdateMemberDto dto);

    MemberProfileDto memberToMemberProfileDto(Member member);

    Member memberProfileDtoToMember(MemberProfileDto dto);

}
