package com.kube.noon.member.binder;


import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.domain.Search;
import com.kube.noon.member.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberBinder {

    MemberBinder INSTANCE = Mappers.getMapper(MemberBinder.class);

    Member AddMemberDtotoEntity(AddMemberDto dto);

    MemberRelationship AddMemberRelationshiptoEntity(AddMemberRelationshipDto dto);

    Member MemberProfiletoEntity(MemberProfileDto dto);

    Search SearchDtoToEntity(SearchDto dto);

    Member UpdateMemberDtoToEntity(UpdateMemberDto dto);

}
