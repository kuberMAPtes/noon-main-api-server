package com.kube.noon.building.dto;

import com.kube.noon.member.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class MemberBuildingSubscriptionResponseDto {
    private final BuildingDto building;
    private final MemberDto subscriptionProvider;
}
