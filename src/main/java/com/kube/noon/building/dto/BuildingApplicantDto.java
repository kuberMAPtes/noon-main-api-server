package com.kube.noon.building.dto;

import com.kube.noon.building.domain.Building;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildingApplicantDto {

    private String memberId;
    private String buildingName;
    private String roadAddr;
    private Double longitude;
    private Double latitude;
    private boolean profileActivated;

}
