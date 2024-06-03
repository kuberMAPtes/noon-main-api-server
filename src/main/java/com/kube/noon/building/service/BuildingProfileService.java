package com.kube.noon.building.service;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.common.zzim.Zzim;

import java.util.List;

public interface BuildingProfileService {

    Zzim addSubscription(String memberId, int buildingId);
    Zzim deleteSubscription(String memberId, int buildingId);
    List<BuildingDto> getUserBuildingSubscriptionList(String memberId);
    Building getBuildingProfile(int buildingId);
    List<BuildingDto> getBuildingSubscriptionListByMemberId(String memberId);
}