package com.kube.noon.building.service;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.repository.mapper.BuildingProfileMapper;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    @Qualifier("buildingProfileRepository")
    private BuildingProfileRepository buildingProfileRepository;

    @Autowired
    @Qualifier("zzimRepository")
    private ZzimRepository zzimRepository;

    @Autowired
    @Qualifier("buildingProfileMapper")
    private BuildingProfileMapper buildingProfileMapper;



    /**
     * 본인이 직접 건물을 구독하는 기능
     * 구독했다 취소한 History가 있다면, 그 레코드의 activated만 수정한다.
     *
     * @param memberId 구독하려는 회원아이디
     * @param buildingId 구독하려는 빌딩아이디
     * @return
     *
     * @author 허예지
     */
    @Transactional
    public Zzim addSubscription(String memberId, int buildingId) {

        boolean historyExist = zzimRepository.existsByBuildingIdAndMemberIdAndSubscriptionProviderId(buildingId, memberId, memberId);

        if(!historyExist){
            Zzim zzim = Zzim.builder()
                    .memberId(memberId)
                    .feedId(null)
                    .buildingId(buildingId)
                    .subscriptionProviderId(memberId)
                    .zzimType("SUBSCRIPTION")
                    .activated(true)
                    .build();

            return zzimRepository.save(zzim);

        }else{

            zzimRepository.updateActivatedByBuildingIdAndMemberIdAndSubscriptionProviderId(buildingId, memberId, true);
            return zzimRepository.findByBuildingIdAndMemberId(buildingId, memberId);

        }
    }

    /**
     * 특정 건물에 대한 구독을 취소한다.
     * 본인이 직접 구독했는지, 건물합치기로 구독했는지는 구분하지 않는다. (한 건물에 대한 구독레코드는 어떤 방식으로든 1개이므로)
     *
     * @return activated가 false가 된 Zzim엔티티
     *
     * @author 허예지
     */
    public Zzim deleteSubscription(String memberId, int buildingId) {

        zzimRepository.updateActivatedByBuildingIdAndMemberIdAndSubscriptionProviderId(buildingId, memberId, false);

        return zzimRepository.findByBuildingIdAndMemberId(buildingId, memberId);
    }



    @Override
    public List<BuildingDto> getUserBuildingSubscriptionList(String memberId) {
        List<Building> buildings = buildingProfileMapper.findBuildingSubscriptionListByMemberId(memberId);

        return buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Building getBuildingProfile(int buildingId) {
        return buildingProfileRepository.findBuildingProfileByBuildingId(buildingId);
    }

    @Override
    public List<BuildingDto> getBuildingSubscriptionListByMemberId(String memberId) {
        List<Building> buildings = buildingProfileMapper.findBuildingSubscriptionListByMemberId(memberId);
        return buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());
    }
}
