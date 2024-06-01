package com.kube.noon.building.service;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.common.zzim.ZzimRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 빌딩 프로필이 Service의 구현체
 *
 * @author 허예지
 */
@Service("buildingProfileServiceImpl")
public class BuildingProfileServiceImpl implements BuildingProfileService {

    @Qualifier("buildingProfileRepository")
    private BuildingProfileRepository buildingProfileRepository;

    @Qualifier("zzimRepository")
    private ZzimRepository zzimRepository;






    @Override
    public void manageSubscription(String memberId, int buildingId, boolean subscribe) {

        /*
        List<String> subscriptions = userSubscriptions.getOrDefault(userId, new ArrayList<>());
        if (subscribe) {
            if (!subscriptions.contains(buildingId)) {
                buildingProfileRepository.save();
            }
        } else {
            subscriptions.remove(buildingId);
        }
        userSubscriptions.put(userId, subscriptions);

         */
    }

    @Override
    public List<BuildingDto> getUserBuildingSubscriptionList(String memberId) {
        List<Building> buildings = zzimRepository.findBuildingSubscriptionListByMemberId(memberId);

        return buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Building getBuildingProfile(int buildingId) {
        return buildingProfileRepository.findBuildingProfileByBuildingId(buildingId);
    }
}
