package com.kube.noon.building.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@ToString
public class BuildingNotFoundResponseDto {
    private final boolean buildingExisting;
    private final String message;
}
