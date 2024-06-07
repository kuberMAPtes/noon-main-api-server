package com.kube.noon.member.binder.mapper;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.MemberRelationshipDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberRelationshipDtoBinder extends Binder<MemberRelationshipDto, MemberRelationship> {

    @Override
    default MemberRelationship toEntity(MemberRelationshipDto dto) {
        throw new UnsupportedOperationException("This method should not be called");
    }

    @Override
    default MemberRelationshipDto toDto(MemberRelationship memberRelationship) {
        throw new UnsupportedOperationException("This method should not be called");
    }

    @Override
    default Class<MemberRelationshipDto> getDtoType() {
        return MemberRelationshipDto.class;
    }
}
