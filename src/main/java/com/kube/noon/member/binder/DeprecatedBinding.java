package com.kube.noon.member.binder;


import com.kube.noon.member.domain.Member;
import com.kube.noon.member.domain.MemberRelationship;
import com.kube.noon.member.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeprecatedBinding {

    DeprecatedBinding INSTANCE = Mappers.getMapper(DeprecatedBinding.class);

    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "unlockTime", ignore = true)
    @Mapping(target = "profilePhotoUrl", ignore = true)
    @Mapping(target = "profileIntro", ignore = true)
    @Mapping(target = "dajungScore", ignore = true)
    @Mapping(target = "signedOff", ignore = true)
    @Mapping(target = "buildingSubscriptionPublicRange", ignore = true)
    @Mapping(target = "allFeedPublicRange", ignore = true)
    @Mapping(target = "memberProfilePublicRange", ignore = true)
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true)
    Member addMemberDtoToMember(AddMemberDto dto);

    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "unlockTime", ignore = true)
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true)
    Member memberProfileDtoToMember(MemberProfileDto dto);
    
    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "signedOff", ignore = true)
    Member updateMemberDtoToMember(UpdateMemberDto dto);
    
    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "unlockTime", ignore = true)
    @Mapping(target = "profilePhotoUrl", ignore = true)
    @Mapping(target = "profileIntro", ignore = true)
    @Mapping(target = "dajungScore", ignore = true)
    @Mapping(target = "signedOff", ignore = true)
    @Mapping(target = "buildingSubscriptionPublicRange", ignore = true)
    @Mapping(target = "allFeedPublicRange", ignore = true)
    @Mapping(target = "memberProfilePublicRange", ignore = true)
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true)
    Member updatePasswordDtoToMember(UpdatePasswordDto dto);
    
    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "unlockTime", ignore = true)
    @Mapping(target = "profilePhotoUrl", ignore = true)
    @Mapping(target = "profileIntro", ignore = true)
    @Mapping(target = "dajungScore", ignore = true)
    @Mapping(target = "signedOff", ignore = true)
    @Mapping(target = "buildingSubscriptionPublicRange", ignore = true)
    @Mapping(target = "allFeedPublicRange", ignore = true)
    @Mapping(target = "memberProfilePublicRange", ignore = true)
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true)
    Member updatePhoneNumberDtoToMember(UpdatePhoneNumberDto dto);


    @Mapping(target = "memberRole", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "unlockTime", ignore = true)
    @Mapping(target = "profileIntro", ignore = true)
    @Mapping(target = "dajungScore", ignore = true)
    @Mapping(target = "signedOff", ignore = true)
    @Mapping(target = "buildingSubscriptionPublicRange", ignore = true)
    @Mapping(target = "allFeedPublicRange", ignore = true)
    @Mapping(target = "memberProfilePublicRange", ignore = true)
    @Mapping(target = "receivingAllNotificationAllowed", ignore = true)
    Member updateMemberProfilePhotoUrlDtoToMember(UpdateMemberProfilePhotoUrlDto dto);

    AddMemberDto memberToAddMemberDto(Member member);
    MemberProfileDto memberToMemberProfileDto(Member member);
    UpdateMemberDto memberToUpdateMemberDto(Member member);
    UpdatePasswordDto memberToUpdatePasswordDto(Member member);
    UpdatePhoneNumberDto memberToUpdatePhoneNumberDto(Member member);
    UpdateMemberProfilePhotoUrlDto memberToUpdateMemberProfilePhotoUrlDto(Member member);

    // 공통 변환 메서드 추가
    default <T> Member toMember(T dto) {
        if (dto instanceof AddMemberDto) {
            return addMemberDtoToMember((AddMemberDto) dto);
        } else if (dto instanceof MemberProfileDto) {
            return memberProfileDtoToMember((MemberProfileDto) dto);
        } else if (dto instanceof UpdateMemberDto) {
            return updateMemberDtoToMember((UpdateMemberDto) dto);
        } else if (dto instanceof UpdatePasswordDto) {
            return updatePasswordDtoToMember((UpdatePasswordDto) dto);
        } else if (dto instanceof UpdatePhoneNumberDto) {
            return updatePhoneNumberDtoToMember((UpdatePhoneNumberDto) dto);
        } else if (dto instanceof UpdateMemberProfilePhotoUrlDto) {
            return updateMemberProfilePhotoUrlDtoToMember((UpdateMemberProfilePhotoUrlDto) dto);
        }
        else {
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
        } else if (dtoClass.equals(UpdatePasswordDto.class)) {
            return dtoClass.cast(memberToUpdatePasswordDto(member));
        } else if (dtoClass.equals(UpdatePhoneNumberDto.class)) {
            return dtoClass.cast(memberToUpdatePhoneNumberDto(member));
        } else if (dtoClass.equals(UpdateMemberProfilePhotoUrlDto.class)) {
            return dtoClass.cast(memberToUpdateMemberProfilePhotoUrlDto(member));
        }
        else {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dtoClass);
        }
    }

    @Mapping(target = "fromMember", source = "fromId", qualifiedByName = "toMemberEntity")
    @Mapping(target = "toMember", source = "toId", qualifiedByName = "toMemberEntity")
    MemberRelationship addMemberRelationshipDtoToMemberRelationship(MemberRelationshipDto dto);

    @Mapping(source = "fromMember.memberId", target = "fromId")
    @Mapping(source = "toMember.memberId", target = "toId")
    MemberRelationshipDto memberRelationshipToAddMemberRelationshipDto(MemberRelationship memberRelationship);

    @Named("toMemberEntity")
    default Member toMemberEntity(String memberId) {
        if (memberId == null) {
            return null;
        }
        Member member = new Member();
        member.setMemberId(memberId);
        return member;
    }

    default <T> MemberRelationship toMemberRelationship(T dto) {
        if (dto instanceof MemberRelationshipDto){
            return addMemberRelationshipDtoToMemberRelationship(((MemberRelationshipDto) dto));
        } else {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dto.getClass());
        }
    }
    default <T> T toDto(MemberRelationship memberRelationship, Class<T> dtoClass){
        if (dtoClass.equals(MemberRelationshipDto.class)){
            return dtoClass.cast(memberRelationshipToAddMemberRelationshipDto(memberRelationship));
        } else {
            throw new IllegalArgumentException("지원되지 않는 DTO 타입입니다: " + dtoClass);
        }
    }


}
