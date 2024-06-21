package com.kube.noon.building.dto;

import com.kube.noon.chat.dto.LiveliestChatroomDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BuildingSearchResponseDto {
    private int buildingId;
    private String buildingName;
    private String roadAddr;
    private String feedAiSummary;
    private LiveliestChatroomDto liveliestChatroomDto;
}
