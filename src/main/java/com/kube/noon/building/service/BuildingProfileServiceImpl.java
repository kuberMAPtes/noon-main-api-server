package com.kube.noon.building.service;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.*;
import com.kube.noon.building.exception.NotRegisteredBuildingException;
import com.kube.noon.building.repository.BuildingSummaryRepository;
import com.kube.noon.building.repository.mapper.BuildingProfileMapper;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.building.service.buildingwiki.BuildingWikiEmptyServiceImpl;
import com.kube.noon.building.service.buildingwiki.BuildingWikiRestTemplateServiceImpl;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.dto.LiveliestChatroomDto;
import com.kube.noon.chat.serviceImpl.ChatroomSearchServiceImpl;
import com.kube.noon.common.constant.PagingConstants;
import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.member.binder.mapper.member.MemberDtoBinderImpl;
import com.kube.noon.member.dto.member.MemberDto;
import com.kube.noon.member.repository.impl.MemberRepositoryImpl;
import com.kube.noon.places.domain.Position;
import com.kube.noon.places.domain.PositionRange;
import com.kube.noon.places.dto.PlaceDto;
import com.kube.noon.places.exception.PlaceNotFoundException;
import com.kube.noon.places.service.PlacesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 빌딩 프로필이 Service의 구현체
 *
 * @author 허예지
 */
@Service("buildingProfileServiceImpl")
@RequiredArgsConstructor
public class BuildingProfileServiceImpl implements BuildingProfileService {


    private static final Logger log = LoggerFactory.getLogger(BuildingProfileServiceImpl.class);
    ///Field
    private final BuildingProfileRepository buildingProfileRepository;
    private final ZzimRepository zzimRepository;
    private final BuildingProfileMapper buildingProfileMapper;
    private final BuildingSummaryRepository buildingSummaryRepository;
    private final FeedRepository feedRepository;
    private final PlacesService placesService;
    private final BuildingWikiService buildingWikiService;


    public static final int SUMMARY_LENGTH_LIMIT = 2000;
    public static final int SUMMARY_FEED_COUNT_LIMIT  = 10;
    private final SortHandlerMethodArgumentResolverCustomizer sortCustomizer;
    private final MemberRepositoryImpl memberRepositoryImpl;
    private final MemberDtoBinderImpl memberDtoBinderImpl;
    private final ChatroomSearchServiceImpl chatroomSearchService;

    private static final int CHART_DATA_LIMIT = 20;

    @Value("${profile-activation.threshold}")
    private int activationThreshold;



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
     * 건물 등록 신청
     * @param buildingApplicantDto
     * @return
     */
    @Transactional
    public BuildingDto addSubscription(BuildingApplicantDto buildingApplicantDto) {

        Building building = buildingProfileRepository.findAppliedBuildingByRoadAddr(buildingApplicantDto.getRoadAddr());

        log.info("도로명 주소로 조회한 빌딩={}", building);

        //첫번째 빌딩 프로필 신청자일때
        if(building==null){

            building = Building.builder()
                    .buildingName(buildingApplicantDto.getBuildingName())
                    .roadAddr(buildingApplicantDto.getRoadAddr())
                    .longitude(buildingApplicantDto.getLongitude())
                    .latitude(buildingApplicantDto.getLatitude())
                    .profileActivated(false)
                    .build();

            buildingProfileRepository.save(building);
            log.info("saved buildingId={}",building.getBuildingId());

        }

        //건물 등록 신청 처리(=구독)
        BuildingZzimDto buildingZzimDto = this.addSubscription(buildingApplicantDto.getMemberId(), building.getBuildingId());

        log.info("현재 구독자수={}",this.getSubscriberCnt(building.getBuildingId()) );

        //건물 프로필 및 위키페이지 생성
        if( this.getSubscriberCnt(building.getBuildingId()) >= activationThreshold){
            building.setProfileActivated(true);
            this.buildingWikiService.addPage(building.getBuildingId()); //테스트용
        }

        return BuildingDto.fromEntity(building);

    }

    /**
     * 건물 아이디로 구독자 목록 조회
     */
    @Override
    public List<MemberDto> getSubscribers(int buildingId) {

        List<String> subscriberIds = zzimRepository.findMemberIdsByBuildingId(buildingId);

        List<MemberDto> subscribers = new ArrayList<>();
        for(String memberId : subscriberIds){
            subscribers.add(memberDtoBinderImpl.toDto(memberRepositoryImpl.findMemberById(memberId).orElseThrow()));
        }
        log.info("Building subscribers={}",subscribers);

        return subscribers;
    }



    /**
     * 도로명 주소로 구독자(or건물 프로필 등록 신청자) 목록 조회
     */
    @Override
    public List<MemberDto> getSubscribers(String roadAddr) {

        log.info("해당 도로명 주소로 조회={}", roadAddr);

        Building building = buildingProfileRepository.findBuildingProfileByRoadAddr(roadAddr);

        if(building==null){
            log.info("처음 신청된 건물={}", roadAddr);
            return null;
        }

        List<String> subscriberIds = zzimRepository.findMemberIdsByBuildingId(building.getBuildingId());

        List<MemberDto> subscribers = new ArrayList<>();
        for(String memberId : subscriberIds){
            subscribers.add(memberDtoBinderImpl.toDto(memberRepositoryImpl.findMemberById(memberId).orElseThrow()));
        }
        log.info("Building subscribers={}",subscribers);

        return subscribers;
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
    public BuildingDto getBuildingProfileByRoadAddr(String roadAddr) {

        Building building = buildingProfileRepository.findBuildingProfileByRoadAddr(roadAddr);

        log.info("findBuildingByRoadAddr result={}", building);

        return BuildingDto.fromEntity(building);
    }

    @Override
    public BuildingDto getBuildingProfileByPosition(Position position)
            throws PlaceNotFoundException, NotRegisteredBuildingException {
        PlaceDto findPlace = this.placesService.getPlaceByPosition(position);
        Building building = this.buildingProfileRepository.findBuildingProfileByRoadAddr(findPlace.getRoadAddress());
        if (building == null) {
            throw new NotRegisteredBuildingException("No building", findPlace);
        }
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
        String feedAiSummary = new JSONObject( buildingSummaryRepository.findFeedAISummary(title, feedText) ).getString("summary");
        log.info("피드 요약 결과={}",feedAiSummary);


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
        List<Building> buildings = buildingProfileRepository.findActivatedBuildings();

        // 지도 범위 내 건물별 구독자 수 기록
        for (Building building : buildings){

            if(isWithinRange(positionRange, building.getLongitude(), building.getLatitude())){
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

    public static boolean isWithinRange(PositionRange range, double buildingLon, double buildingLat) {
        return (buildingLat >= range.getLowerLatitude() && buildingLat <= range.getUpperLatitude())
                && (buildingLon >= range.getLowerLongitude() && buildingLon <= range.getUpperLongitude());
    }

    @Override
    public List<BuildingSearchResponseDto> searchBuilding(String searchKeyword, Integer page) {
        page = page == null ? PagingConstants.DEFAULT_PAGE : page;
        return this.buildingProfileRepository
                .findBuildingProfileBySearchKeyword(searchKeyword, PagingConstants.PAGE_SIZE * (page - 1), PagingConstants.PAGE_SIZE)
                .stream()
                .map((building) -> {
                    BuildingSearchResponseDto dto = new BuildingSearchResponseDto();
                    BeanUtils.copyProperties(building, dto);
                    dto.setLiveliestChatroomDto(new LiveliestChatroomDto("SAMPLE", "SAMPLE")); // TODO: Replace with real data in the future
                    return dto;
                })
                .toList();
    }

    @Override
    public List<BuildingChartDto> getChart(String reqType) throws Exception {

        List<Building> buildingList = buildingProfileRepository.findAll();
        List<BuildingChartDto> buildingChart = new ArrayList<>();

        log.info("All buildings={}",buildingList);

        for(Building building : buildingList){
            BuildingChartDto buildingChartDto = new BuildingChartDto();
            buildingChartDto.setBuildingId(building.getBuildingId());
            buildingChartDto.setBuildingName(building.getBuildingName());

            switch (reqType){
                case "SUBSCRIBER":
                    //건물별 구독자 수 저장
                    buildingChartDto.setCnt(this.getSubscriberCnt(building.getBuildingId()));
                    log.info("buildingChartDto={}", buildingChartDto);
                    break;

                case "FEED":
                    //건물별 피드 개수 저장
                    List<Feed> feedList = feedRepository.findByBuildingAndActivatedTrue(building);
                    buildingChartDto.setCnt(feedList.size());
                    break;

                case "CHAT":
                    //건물별 채팅방 개수 저장
                    List<ChatroomDto> chatroomList = chatroomSearchService.getBuildingChatroomList(building.getBuildingId());
                    buildingChartDto.setCnt(chatroomList.size());
                    break;

                default:
                    break;
            }/// end of switch

            buildingChart.add(buildingChartDto);

            log.info("added data={}",buildingChartDto);
            log.info("added chart={}",buildingChart);
            log.info("chart size={}",buildingChart.size());
        }///end of for

        log.info("buildingChart={}",buildingChart);

        //cnt 기준 내림차순 정렬
        buildingChart.sort(Comparator.comparingInt(BuildingChartDto::getCnt).reversed());
        log.info("sorted buildingChart={}",buildingChart);

        // 높은순 CHART_DATA_LIMIT개 데이터만 제공함
        if (buildingChart.size() > CHART_DATA_LIMIT) {
            buildingChart = buildingChart.subList(0, CHART_DATA_LIMIT);
        }

        return buildingChart;
    }
}
