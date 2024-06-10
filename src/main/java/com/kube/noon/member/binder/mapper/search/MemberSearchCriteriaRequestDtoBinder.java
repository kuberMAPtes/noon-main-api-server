package com.kube.noon.member.binder.mapper.search;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.dto.search.MemberSearchCriteriaDto;
import com.kube.noon.member.dto.RequestDto.MemberSearchCriteriaRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberSearchCriteriaRequestDtoBinder extends Binder<MemberSearchCriteriaRequestDto, MemberSearchCriteriaDto> {

    @Override
    MemberSearchCriteriaDto toOtherDto(MemberSearchCriteriaRequestDto dto);

}
