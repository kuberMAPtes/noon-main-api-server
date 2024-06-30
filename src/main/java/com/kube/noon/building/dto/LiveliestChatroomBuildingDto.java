package com.kube.noon.building.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class LiveliestChatroomBuildingDto {
    private final BuildingDto building;
    private final int chatroomId;
    private final String chatroomName;
    private final int liveliness;
}
