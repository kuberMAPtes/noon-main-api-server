package com.kube.noon.member.binder.mapper.toMemberRelationship;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.AddMemberRelationshipDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddMemberRelationshipDtoBinder extends Binder<AddMemberRelationshipDto, MemberRelationship> {

    @Override
    @Mapping(target = "fromMember", source = "fromId", qualifiedByName = "toMemberEntity")
    @Mapping(target = "toMember", source = "toId", qualifiedByName = "toMemberEntity")
    MemberRelationship toEntity(AddMemberRelationshipDto dto);

    @Override
    @Mapping(source = "fromMember.memberId", target = "fromId")
    @Mapping(source = "toMember.memberId", target = "toId")
    AddMemberRelationshipDto toDto(MemberRelationship memberRelationship);

    /**
     * source > memberId target > Member로 메서드를 호출한다.
     * @param memberId
     * @return
     */
    @Named("toMemberEntity")
    default Member toMemberEntity(String memberId) {
        if (memberId == null) {
            return null;
        }
        Member member = new Member();
        member.setMemberId(memberId);
        return member;
    }
}


