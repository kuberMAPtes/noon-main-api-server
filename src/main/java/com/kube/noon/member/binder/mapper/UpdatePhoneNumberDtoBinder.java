package com.kube.noon.member.binder.mapper;

import com.kube.noon.common.binder.Binder;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.dto.UpdatePhoneNumberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UpdatePhoneNumberDtoBinder extends Binder<UpdatePhoneNumberDto,Member> {

    @Override
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
    Member toEntity(UpdatePhoneNumberDto dto);

    @Override
    UpdatePhoneNumberDto toDto(Member member);

}
