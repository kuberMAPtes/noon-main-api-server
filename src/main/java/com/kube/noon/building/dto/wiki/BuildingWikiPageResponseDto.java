package com.kube.noon.building.dto.wiki;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class BuildingWikiPageResponseDto {
    private final String buildingName;
    private final String htmlContent;
}
