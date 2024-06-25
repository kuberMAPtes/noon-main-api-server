package com.kube.noon.building.dto;

import com.kube.noon.places.dto.PlaceDto;
import lombok.*;

@AllArgsConstructor
@Getter
@ToString
public class BuildingNotFoundResponseDto {
    private final boolean buildingExisting;
    private final String message;
    private final PlaceDto place;
}
