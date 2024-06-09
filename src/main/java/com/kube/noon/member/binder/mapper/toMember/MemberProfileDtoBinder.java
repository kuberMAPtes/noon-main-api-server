package com.kube.noon.member.binder.mapper.toMember;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.MemberProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberProfileDtoBinder extends Binder<MemberProfileDto,Member> {

    @Override
    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "unlockTime", ignore = true)
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true)
    Member toEntity(MemberProfileDto dto);

    @Override
    MemberProfileDto toDto(Member member);

}
