package com.kube.noon.building.service;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.dto.BuildingZzimDto;
import com.kube.noon.building.repository.BuildingSummaryRepository;
import com.kube.noon.building.repository.mapper.BuildingProfileMapper;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.repository.FeedRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 빌딩 프로필이 Service의 구현체
 *
 * @author 허예지
 */
@Service("buildingProfileServiceImpl")
@RequiredArgsConstructor
public class BuildingProfileServiceImpl implements BuildingProfileService {


    ///Field
    private final BuildingProfileRepository buildingProfileRepository;
    private final ZzimRepository zzimRepository;
    private final BuildingProfileMapper buildingProfileMapper;
    private final BuildingSummaryRepository buildingSummaryRepository;
    private final FeedRepository feedRepository;

    public static final int SUMMARY_LENGTH_LIMIT = 2000;
    public static final int SUMMARY_FEED_COUNT_LIMIT  = 10;

    ///Method
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
    public BuildingZzimDto addSubscription(String memberId, int buildingId) {

        boolean historyExist = zzimRepository.existsByBuildingIdAndMemberIdAndSubscriptionProviderId(buildingId, memberId, memberId);

        if(!historyExist){
            Zzim zzim = Zzim.builder()
                    .memberId(memberId)
                    .feedId(null)
                    .buildingId(buildingId)
                    .subscriptionProviderId(memberId)
                    .zzimType(ZzimType.SUBSCRIPTION)
                    .activated(true)
                    .build();

            return BuildingZzimDto.fromEntity(zzimRepository.save(zzim));

        }else{

            zzimRepository.updateZzimActivated(buildingId, memberId, true);
            return BuildingZzimDto.fromEntity(zzimRepository.findByBuildingIdAndMemberId(buildingId, memberId));

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
    public BuildingZzimDto deleteSubscription(String memberId, int buildingId) {

        zzimRepository.updateZzimActivated(buildingId, memberId, false);

        Zzim zzim = zzimRepository.findByBuildingIdAndMemberId(buildingId, memberId);

        return BuildingZzimDto.fromEntity(zzim);
    }





    /**
     * 타회원의 구독 목록을 회원의 구독목록에 합친다.
     * 타회원 구독 목록 중 회원이 이미 직접 구독한 빌딩은 회원 구독목록에 중복으로 추가하지 않는다.
     *
     * @param memberId 타회원의 구독목록을 가져오려는 회원의 계정아이디
     * @param someoneId 구독목록을 제공하는 회원의 계정아이디
     * @return 타회원 구독 목록이 합쳐진 회원의 구독 목록
     * 
     * @Author 허예지
     */
    @Override
    public List<BuildingDto> addSubscriptionFromSomeone(String memberId, String someoneId) {
        List<Building> mySubscriptionList = buildingProfileMapper.findBuildingSubscriptionListByMemberId(memberId);
        List<Building> someoneSubscriptionList = buildingProfileMapper.findBuildingSubscriptionListByMemberId(someoneId);
        List<Zzim> subscriptionsToAdd = new ArrayList<>();

        for(Building building : someoneSubscriptionList){
            if(!mySubscriptionList.contains(building)){

                subscriptionsToAdd.add(Zzim.builder()
                        .memberId(memberId)
                        .feedId(null)
                        .buildingId(building.getBuildingId())
                        .subscriptionProviderId(someoneId)
                        .zzimType(ZzimType.SUBSCRIPTION)
                        .activated(true)
                        .build());

            }
        }

        zzimRepository.saveAll(subscriptionsToAdd);

        List<Building> buildings = buildingProfileMapper.findBuildingSubscriptionListByMemberId(memberId);

        return buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());

    }




    @Override
    public List<BuildingDto> getUserBuildingSubscriptionList(String memberId) {
        List<Building> buildings = buildingProfileMapper.findBuildingSubscriptionListByMemberId(memberId);

        return buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public BuildingDto getBuildingProfile(int buildingId) {
        Building building = buildingProfileRepository.findBuildingProfileByBuildingId(buildingId);
        return BuildingDto.fromEntity(building);
    }

    @Override
    public List<BuildingDto> getBuildingSubscriptionListByMemberId(String memberId) {
        List<Building> buildings = buildingProfileMapper.findBuildingSubscriptionListByMemberId(memberId);
        return buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * buildingId로 조회한 빌딩의 모든 피드를 가져와 3줄로 요약
     * 임시데이터 피드 10개로 요약. 추후 피드 서비스인 '최신 피드목록 가져오기'로 대체
     *
     * @param buildingId 피드 요약을 하려는 빌딩의 아이디
     * @return
     */
    @Override
    public String getFeedAISummary(int buildingId) {

        List<Feed> getFeedListByBuildingId = feedRepository.findByBuildingAndActivatedTrue(Building.builder().buildingId(buildingId).build());

        String title = "";
        String feedText = "";

        for(Feed feed : getFeedListByBuildingId){

            // CLOVA Summary 요약 글자 제한: 제목+내용 2000자
            if( (title+feed.getTitle()+feedText+feed.getFeedText()).length() > SUMMARY_LENGTH_LIMIT ){

                title="";
                feedText = buildingSummaryRepository.findFeedAISummary(title, feedText);

            }else{
                title += (feed.getTitle()+". ");
                feedText += (feed.getFeedText()+". ");
            }

        }///end of for

        return buildingSummaryRepository.findFeedAISummary(title, feedText);
    }
}
