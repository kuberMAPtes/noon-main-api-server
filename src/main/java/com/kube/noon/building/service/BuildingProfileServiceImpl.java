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
import com.kube.noon.places.domain.PositionRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final SortHandlerMethodArgumentResolverCustomizer sortCustomizer;

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
    public List<BuildingDto> getMemberBuildingSubscriptionList(String memberId) {
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

        System.out.println("Call getFeedAISummary...");

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

        // 해당 빌딩의 요약 받아오기
        String feedAiSummary = buildingSummaryRepository.findFeedAISummary(title, feedText);

        // 해당 빌딩의 요약 업데이트
        Building building = buildingProfileRepository.findBuildingProfileByBuildingId(buildingId);
        building.setFeedAiSummary(feedAiSummary);
        buildingProfileRepository.save(building);

        return feedAiSummary;
    }


    /**
     * 건물별 구독자 수를 조회한다.
     * @param buildingId 구독자 수를 조회할 건물 아이디
     * @return 구독자 수
     */
    @Override
    public int getSubscriberCnt(int buildingId) {
        return zzimRepository.countByBuildingIdAndActivated(buildingId, true);
    }


    /**
     * 위치범위(사용자 화면) 내 건물 중 구독자가 많은 10개의 건물을 가져온다.
     *
     * @param positionRange 사용자의 화면 범위
     * @return
     */
    @Override
    public List<BuildingDto> getBuildingsWithinRange(PositionRange positionRange) {

        Map<Building, Integer> subscriberCntMap = new HashMap<>();
        Map<String,Double> range = getRange(positionRange);
        List<Building> buildings = buildingProfileRepository.findActivatedBuildings();

        // 지도 범위 내 건물별 구독자 수 기록
        for (Building building : buildings){

            if(isWithinRange(range, building.getLongitude(), building.getLatitude())){
                subscriberCntMap.put(building, this.getSubscriberCnt(building.getBuildingId()));
            }

        }

        // 구독자 많은순으로 정렬
        List<Map.Entry<Building, Integer>> listForSorting = new ArrayList<>(subscriberCntMap.entrySet());
        listForSorting.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));


        // 결과 추출
        List<Building> popularBuildings = new ArrayList<>();
        for (int i = 0; i < Math.min(10, listForSorting.size()); i++) {
            popularBuildings.add(listForSorting.get(i).getKey());
        }

        return popularBuildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());

    }


    public Map<String,Double> getRange(PositionRange positionRange){
        double minLat = Math.min(Math.min(positionRange.getNe().getLatitude(),positionRange.getNw().getLatitude()), Math.min(positionRange.getSe().getLatitude(), positionRange.getSw().getLatitude()));
        double maxLat = Math.max(Math.max(positionRange.getNe().getLatitude(),positionRange.getNw().getLatitude()), Math.min(positionRange.getSe().getLatitude(), positionRange.getSw().getLatitude()));
        double minLon = Math.min(Math.min(positionRange.getNe().getLatitude(),positionRange.getNw().getLatitude()), Math.min(positionRange.getSe().getLatitude(), positionRange.getSw().getLatitude()));
        double maxLon = Math.max(Math.max(positionRange.getNe().getLatitude(),positionRange.getNw().getLatitude()), Math.max(positionRange.getSe().getLatitude(), positionRange.getSw().getLatitude()));

        Map<String,Double> range = new HashMap<>();
        range.put("minLat", minLat);
        range.put("maxLat", maxLat);
        range.put("minLon", minLon);
        range.put("maxLon", maxLon);

        return range;
    }

    public static boolean isWithinRange(Map<String,Double> range, double buildingLon, double buildingLat) {
        return (buildingLat >= range.get("minLat") && buildingLat <= range.get("maxLat")) && (buildingLon >= range.get("minLon") && buildingLon <= range.get("maxLon"));
    }


}
