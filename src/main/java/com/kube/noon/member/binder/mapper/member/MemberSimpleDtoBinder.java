package com.kube.noon.member.binder.mapper.member;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.member.MemberDto;
import com.kube.noon.member.dto.member.MemberSimpleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberSimpleDtoBinder extends Binder<MemberSimpleDto, Member> {

    @Override
    public MemberSimpleDto toDto(Member member);

}
