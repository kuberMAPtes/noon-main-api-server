package com.kube.noon.building.dto;

import com.kube.noon.building.domain.Building;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildingDto {
    private int buildingId;
    private String buildingName;
    private boolean profileActivated;
    private String roadAddr;
    private Double longitude;
    private Double latitude;
    private String feedAiSummary;

    public static BuildingDto fromEntity(Building building) {
        return BuildingDto.builder()
                .buildingId(building.getBuildingId())
                .buildingName(building.getBuildingName())
                .profileActivated(building.isProfileActivated())
                .roadAddr(building.getRoadAddr())
                .longitude(building.getLongitude())
                .latitude(building.getLatitude())
                .feedAiSummary(building.getFeedAiSummary())
                .build();
    }

    public Building toEntity() {
        return Building.builder()
                .buildingId(this.buildingId)
                .buildingName(this.buildingName)
                .profileActivated(this.profileActivated)
                .roadAddr(this.roadAddr)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .feedAiSummary(this.feedAiSummary)
                .build();
    }
}
