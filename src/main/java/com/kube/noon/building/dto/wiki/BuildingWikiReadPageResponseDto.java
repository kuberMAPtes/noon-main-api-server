package com.kube.noon.building.dto.wiki;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class BuildingWikiReadPageResponseDto {
    private final String htmlContent;
}
