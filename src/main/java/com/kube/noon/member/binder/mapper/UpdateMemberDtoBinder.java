package com.kube.noon.member.binder.mapper;

import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.UpdateMemberDto;
import com.kube.noon.member.binder.Binder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UpdateMemberDtoBinder extends Binder<UpdateMemberDto>{

    @Override
    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "signedOff", ignore = true)
    Member toEntity(UpdateMemberDto dto);

    @Override
    UpdateMemberDto toDto(Member member);

    @Override
    default Class<UpdateMemberDto> getDtoType() {
        return UpdateMemberDto.class;
    }

}
