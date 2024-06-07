package com.kube.noon.member.binder.mapper;

import com.kube.noon.member.binder.Binder;
import com.kube.noon.member.dto.MemberRelationshipDto;
import org.mapstruct.Mapper;

@Mapper
public interface MemberRelationshipDtoBinder extends Binder<MemberRelationshipDto> {

//    @Override
//    MemberRelationship toEntity(MemberRelationshipDto dto);
//
//    @Override
//    MemberRelationshipDto toDto(MemberRelationship memberRelationship);
//
//    @Override
//    default Class<MemberRelationshipDto> getDtoType() {
//        return MemberRelationshipDto.class;
//    }


}
