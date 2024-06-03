package com.kube.noon.member.binder;


import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.AddMemberDto;
import com.kube.noon.member.dto.AddMemberRelationshipDto;
import com.kube.noon.member.dto.MemberProfileDto;
import com.kube.noon.member.dto.UpdateMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberBinder {

    MemberBinder INSTANCE = Mappers.getMapper(MemberBinder.class);

    @Mapping(target = "memberRole", ignore = true) // Default value 설정
    @Mapping(target = "unlockTime", ignore = true) // Default value 설정
    @Mapping(target = "dajungScore", ignore = true) // Default value 설정
    @Mapping(target = "signedOff", ignore = true) // Default value 설정
    @Mapping(target = "buildingSubscriptionPublicRange", ignore = true) // Default value 설정
    @Mapping(target = "allFeedPublicRange", ignore = true) // Default value 설정
    @Mapping(target = "memberProfilePublicRange", ignore = true) // Default value 설정
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true) // Default value 설정
    @Mapping(target = "profilePhotoUrl", ignore = true) // NULL 허용
    @Mapping(target = "profileIntro", ignore = true) // NULL 허용
    Member addMemberDtoToMember(AddMemberDto dto);

    AddMemberDto memberToAddMemberDto(Member member);

    @Mapping(target = "memberRole", ignore = true) // Default value 설정
    @Mapping(target = "pwd", ignore = true) // Default value 설정
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "unlockTime", ignore = true) // Default value 설정
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true)
    Member memberProfileDtoToMember(MemberProfileDto dto);

    MemberProfileDto memberToMemberProfileDto(Member member);

    @Mapping(target = "memberRole", ignore = true) // Default value 설정
    @Mapping(target = "pwd", ignore = true) // Default value 설정
    @Mapping(target = "signedOff", ignore = true) // Default value 설정
    Member updateMemberDtoToMember(UpdateMemberDto dto);

    UpdateMemberDto memberToUpdateMemberDto(Member member);

    @Mapping(target = "fromMember", source = "fromId", qualifiedByName = "toMemberEntity")
    @Mapping(target = "toMember", source = "toId", qualifiedByName = "toMemberEntity")
    MemberRelationship addMemberRelationshipDtoToMemberRelationship(AddMemberRelationshipDto dto);

    @Mapping(source = "fromMember.memberId", target = "fromId")
    @Mapping(source = "toMember.memberId", target = "toId")
    AddMemberRelationshipDto memberRelationshipToAddMemberRelationshipDto(MemberRelationship memberRelationship);

    @Named("toMemberEntity")
    default Member toMemberEntity(String memberId) {
        if (memberId == null) {
            return null;
        }
        Member member = new Member();
        member.setMemberId(memberId);
        return member;
    }

    // 공통 변환 메서드 추가
    default <T> Member toMember(T dto) {
        if (dto instanceof AddMemberDto) {
            return addMemberDtoToMember((AddMemberDto) dto);
        } else if (dto instanceof MemberProfileDto) {
            return memberProfileDtoToMember((MemberProfileDto) dto);
        } else if (dto instanceof UpdateMemberDto) {
            return updateMemberDtoToMember((UpdateMemberDto) dto);
        } else {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dto.getClass());
        }
    }
    default <T> T toDto(Member member, Class<T> dtoClass) {
        if (dtoClass.equals(AddMemberDto.class)) {
            return dtoClass.cast(memberToAddMemberDto(member));
        } else if (dtoClass.equals(MemberProfileDto.class)) {
            return dtoClass.cast(memberToMemberProfileDto(member));
        } else if (dtoClass.equals(UpdateMemberDto.class)) {
            return dtoClass.cast(memberToUpdateMemberDto(member));
        } else {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dtoClass);
        }
    }
    default <T> MemberRelationship toMemberRelationship(T dto) {
        if (dto instanceof AddMemberRelationshipDto){
            return addMemberRelationshipDtoToMemberRelationship(((AddMemberRelationshipDto) dto));
        } else {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dto.getClass());
        }
    }
    default <T> T toDto(MemberRelationship memberRelationship, Class<T> dtoClass){
        if (dtoClass.equals(AddMemberRelationshipDto.class)){
            return dtoClass.cast(memberRelationshipToAddMemberRelationshipDto(memberRelationship));
        } else {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dtoClass);
        }
    }


}
