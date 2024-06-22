package com.kube.noon.member.binder.mapper.search;


import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.dto.RequestDto.MemberRelationshipSearchCriteriaRequestDto;
import com.kube.noon.member.dto.search.MemberRelationshipSearchCriteriaDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberRelationshipSearchCriteriaRequestDtoBinder extends Binder<MemberRelationshipSearchCriteriaRequestDto, MemberRelationshipSearchCriteriaDto> {

    @Override
    @Mapping(target="memberId", source="toId")
    MemberRelationshipSearchCriteriaDto toOtherDto(MemberRelationshipSearchCriteriaRequestDto dto);

}
