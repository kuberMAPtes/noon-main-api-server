package com.kube.noon.building.service;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;

import java.util.List;

public interface BuildingProfileService {

    void manageSubscription(String userId, int buildingId, boolean subscribe);
    List<BuildingDto> getUserBuildingSubscriptionList(String memberId);
    Building getBuildingProfile(int buildingId);
}