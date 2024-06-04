package com.kube.noon.building.service;

import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.dto.BuildingZzimDto;
import java.util.List;

public interface BuildingProfileService {

    BuildingZzimDto addSubscription(String memberId, int buildingId);
    BuildingZzimDto deleteSubscription(String memberId, int buildingId);
    List<BuildingDto> addSubscriptionFromSomeone(String memberId, String someoneId);
    List<BuildingDto> getUserBuildingSubscriptionList(String memberId);
    BuildingDto getBuildingProfile(int buildingId);
    List<BuildingDto> getBuildingSubscriptionListByMemberId(String memberId);
}