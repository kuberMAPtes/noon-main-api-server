package com.kube.noon.building.dto;

import com.kube.noon.member.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class MemberBuildingSubscriptionResponseDto {
    private final BuildingDto building;
    private final List<MemberDto> subscriptionProviderList;
}
