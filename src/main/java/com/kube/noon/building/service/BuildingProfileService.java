package com.kube.noon.building.service;

import com.kube.noon.building.dto.*;
import com.kube.noon.building.exception.NotRegisteredBuildingException;
import com.kube.noon.member.dto.member.MemberDto;
import com.kube.noon.places.domain.Position;
import com.kube.noon.places.domain.PositionRange;
import com.kube.noon.places.exception.PlaceNotFoundException;

import java.util.List;

public interface BuildingProfileService {

    BuildingZzimDto addSubscription(String memberId, int buildingId);
    BuildingDto addSubscription(BuildingApplicantDto buildingApplicantDto);
    List<MemberDto> getSubscribers(int buildingId);
    List<SubscriberDto> getSubscribers(int buildingId, String viewerId);
    List<MemberDto> getSubscribers(String roadAddr);
    BuildingZzimDto deleteSubscription(String memberId, int buildingId);
    List<BuildingDto> addSubscriptionFromSomeone(String memberId, String someoneId);
    List<MemberBuildingSubscriptionResponseDto> getMemberBuildingSubscriptionList(String memberId);
    List<LiveliestChatroomBuildingDto> getLiveliestChatroomBuilding();

    BuildingDto getBuildingProfile(int buildingId);
    BuildingDto getBuildingProfileByRoadAddr(String roadAddr);
    BuildingDto getBuildingProfileByPosition(Position position) throws PlaceNotFoundException, NotRegisteredBuildingException;
    List<BuildingDto> getBuildingSubscriptionListByMemberId(String memberId);
    List<BuildingDto> getBuildingsWithinRange(PositionRange positionRange);
    String getFeedAISummary(int buildingId);
    int getSubscriberCnt(int buildingId);
    List<BuildingSearchResponseDto> searchBuilding(String searchKeyword, Integer page);
    List<BuildingChartDto> getChart(String reqType) throws Exception;
}