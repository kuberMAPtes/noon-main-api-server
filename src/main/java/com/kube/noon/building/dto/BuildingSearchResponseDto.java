package com.kube.noon.building.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BuildingSearchResponseDto {
    private String buildingName;
    private String roadAddr;
    private String feedAiSummary;
}
