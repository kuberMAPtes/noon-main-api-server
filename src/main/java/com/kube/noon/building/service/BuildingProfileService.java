package com.kube.noon.building.service;

import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.dto.BuildingZzimDto;
import com.kube.noon.common.PublicRange;
import com.kube.noon.places.domain.PositionRange;

import java.util.List;

public interface BuildingProfileService {

    BuildingZzimDto addSubscription(String memberId, int buildingId);
    BuildingZzimDto deleteSubscription(String memberId, int buildingId);
    List<BuildingDto> addSubscriptionFromSomeone(String memberId, String someoneId);
    List<BuildingDto> getMemberBuildingSubscriptionList(String memberId);
    BuildingDto getBuildingProfile(int buildingId);
    List<BuildingDto> getBuildingSubscriptionListByMemberId(String memberId);
    List<BuildingDto> getBuildingsWithinRange(PositionRange positionRange);
    String getFeedAISummary(int buildingId);
    int getSubscriberCnt(int buildingId);

}