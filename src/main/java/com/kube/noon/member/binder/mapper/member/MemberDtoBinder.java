package com.kube.noon.member.binder.mapper.member;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.MemberDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberDtoBinder extends Binder<MemberDto, Member>{
    @Override
    MemberDto toDto(Member member);
    @Override
    Member toEntity(MemberDto dto);
}
