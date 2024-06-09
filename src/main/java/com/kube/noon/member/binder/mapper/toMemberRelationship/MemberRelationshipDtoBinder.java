package com.kube.noon.member.binder.mapper.toMemberRelationship;


import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberRelationshipDtoBinder extends Binder<MemberRelationshipDto, MemberRelationship>{

    @Override
    MemberRelationship toEntity(MemberRelationshipDto dto);

    @Override
    MemberRelationshipDto toDto(MemberRelationship memberRelationship);
}
