package com.kube.noon.member.binder.mapper.memberRelationship;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.common.binder.DtoEntityBinder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.MemberSimpleDto;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipDto;
import com.kube.noon.member.dto.memberRelationship.MemberRelationshipSimpleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MemberRelationshipSimpleDtoBinder extends Binder<MemberRelationshipSimpleDto, MemberRelationshipDto> {

    @Override
    @Mapping(source = "fromMember", target = "fromMember", qualifiedByName = "memberToMemberSimpleDto")
    @Mapping(source = "toMember", target = "toMember", qualifiedByName = "memberToMemberSimpleDto")
    MemberRelationshipSimpleDto toDto(MemberRelationshipDto memberRelationshipDto);

    @Named("memberToMemberSimpleDto")
    default MemberSimpleDto memberToMemberSimpleDto(Member member){
        return DtoEntityBinder.INSTANCE.toDto(member, MemberSimpleDto.class);
    }

}
