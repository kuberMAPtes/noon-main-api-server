package com.kube.noon.building.service;
import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingApplicantDto;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.dto.BuildingSearchResponseDto;
import com.kube.noon.building.dto.BuildingZzimDto;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.member.dto.member.MemberDto;
import com.kube.noon.places.domain.Position;
import com.kube.noon.places.domain.PositionRange;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
@EnableScheduling
public class TestBuildingService {

    @Autowired
    @Qualifier("buildingProfileServiceImpl")
    BuildingProfileService buildingProfileService;


    @DisplayName("멤버아이디, 건물아이디로 구독하기")
    @Test
    void addSubscription() {

        BuildingZzimDto zzimDto = buildingProfileService.addSubscription("member_2",10000);

        log.info("건물아이디={}", zzimDto.getBuildingId());
        log.info("찜타입={}", zzimDto.getZzimType());
        log.info("구독자아이디={}", zzimDto.getMemberId());
        log.info("구독제공자아이디={}", zzimDto.getSubscriptionProviderId());
        log.info("찜activated={}", zzimDto.isActivated());

    }

    @DisplayName("memberId, roadAddr, longitude, latitude로 건물 등록 신청")
    @Test
    void addApplicant() {

        //첫번째 신청자
        BuildingApplicantDto buildingApplicantDto = BuildingApplicantDto.builder()
                .memberId("member_1")
                .buildingName("Sample Building") // 샘플 건물 이름
                .roadAddr("123 Sample Street, Sample City, Sample Country") // 샘플 도로 주소
                .longitude(127.03642) // 샘플 경도 값
                .latitude(37.50124) // 샘플 위도 값
                .profileActivated(false) // 샘플 프로필 활성화 상태
                .build();

        BuildingDto result = buildingProfileService.addSubscription(buildingApplicantDto);
        log.info("Profile activated={}", buildingProfileService.getBuildingProfile(result.getBuildingId()).isProfileActivated());


        //두번째 신청자 (프로필 활성화 기준: 2)
        buildingApplicantDto = BuildingApplicantDto.builder()
                .memberId("member_2")
                .buildingName("Sample Building") // 샘플 건물 이름
                .roadAddr("123 Sample Street, Sample City, Sample Country") // 샘플 도로 주소
                .longitude(127.03642) // 샘플 경도 값
                .latitude(37.50124) // 샘플 위도 값
                .profileActivated(false) // 샘플 프로필 활성화 상태
                .build();

        result = buildingProfileService.addSubscription(buildingApplicantDto);
        log.info("Second subscription={}", result);

        log.info("Profile activated={}", buildingProfileService.getBuildingProfile(result.getBuildingId()).isProfileActivated());


    }

    @DisplayName("건물 구독자 or 건물 등록 신청자 목록 조회")
    @Test
    void getSubscribers(){

        // 건물 아이디로 가져오기
        List<MemberDto> subscribers = buildingProfileService.getSubscribers(10100);

        for(MemberDto member : subscribers){
            log.info("subscriber={}",member);
        }


        // 도로명 주소로 가져오기
        subscribers = buildingProfileService.getSubscribers("123 Sample Street, Sample City, Sample Country");

        for(MemberDto member : subscribers){
            log.info("subscriber={}",member);
        }


    }


    @DisplayName("멤버아이디, 건물아이디로 구독 취소하기")
    @Test
    void deleteSubscription() {

        BuildingZzimDto zzimDto = buildingProfileService.deleteSubscription("member_2",10000);

        log.info("건물아이디={}", zzimDto.getBuildingId());
        log.info("찜타입={}", zzimDto.getZzimType());
        log.info("구독자아이디={}", zzimDto.getMemberId());
        log.info("구독제공자아이디={}", zzimDto.getSubscriptionProviderId());
        log.info("찜activated={}", zzimDto.isActivated());

    }

    @DisplayName("멤버아이디와 타멤버아이디로 타회원 구독 목록 가져오기")
    @Test
    void addSubscriptionFromSomeone() {

        List<BuildingDto> buildingDtos = buildingProfileService.addSubscriptionFromSomeone("member_3", "member_1");

        for (BuildingDto buildingDto : buildingDtos) {

            log.info("빌딩이름={}", buildingDto.getBuildingName());
            log.info("빌딩도로명주소={}", buildingDto.getRoadAddr());

        }

    }


    /**
     * 빌딩 아이디로 특정 빌딩의 요약 업데이트
     * 
     * 또한, ScheduledTasks에서 호출하도록 구현하였다.
     * 그 때 getFeedAISummary 서비스의 파라미터는 application-buildings.properties에 명시된 빌딩아이디들이다.
     *
     */
    @DisplayName("건물아이디로 건물 피드 요약 보기")
    @Test
    void getFeedAISummary() throws InterruptedException {

        log.info("빌딩피드 요약내용={}", buildingProfileService.getFeedAISummary(10099));
      //  Thread.sleep(30000); Scheduled 테스트를 위함. 5초마다 호출하는 것을 확인하기 위해 대기.(실제 앱에서는 매일 24시마다 업데이트)

    }

    @Test
    void getSubscriberCnt(){
        log.info("구독자수={}",buildingProfileService.getSubscriberCnt(10089));
    }


    @Test
    void getBuildingsWithinRange(){

        Position ne = new Position(37.7749, -122.4194); // 북동 (임의의 위도와 경도)
        Position nw = new Position(37.7749, -122.4244); // 북서 (임의의 위도와 경도)
        Position se = new Position(37.7680, -122.4194); // 남동 (임의의 위도와 경도)
        Position sw = new Position(37.7680, -122.4244); // 남서 (임의의 위도와 경도)

        PositionRange positionRange = new PositionRange(37.7680, -122.4194, 37.7749, -122.4244);
        List<BuildingDto> buildingDtos = buildingProfileService.getBuildingsWithinRange(positionRange);
        log.info("buildingDtos={}", buildingDtos);
    }

    @Autowired
    BuildingProfileRepository buildingProfileRepository;

    @Transactional
    @Test
    void searchBuilding() {
        Building.BuildingBuilder buildingBuilder = Building.builder()
                .profileActivated(true)
                .roadAddr("addr")
                .longitude(127.12521124)
                .latitude(35.1525125)
                .feedAiSummary("asdf");

        String[] forTest = {
                "h31hf99ea,fqeh31r",
                "ghi3fh,eht0efaf",
                "ghwa993h1r,asvihoosav",
                "none,voiashvhr",
                "vd83h1fav,none",
                "none,none",
                "never,n"
        };

        for (int i = 0; i < 5; i++) {
            Arrays.stream(forTest)
                    .map((ps) -> ps.split(","))
                    .map((ps) -> {
                        String prefix = ps[0];
                        String suffix = ps[1];
                        if (prefix.equals("never")) {
                            return buildingBuilder.buildingName("NeverMind").build();
                        }

                        String fullName = "sample building";
                        if (!prefix.equals("none")) {
                            fullName = prefix + fullName;
                        }
                        if (!suffix.equals("none")) {
                            fullName = fullName + suffix;
                        }
                        return buildingBuilder.buildingName(fullName).build();
                    }).forEach((b) -> this.buildingProfileRepository.save(b));
        }

        List<BuildingSearchResponseDto> sampleBuildingPage1 =
                this.buildingProfileService.searchBuilding("sample building", 1);
        Assertions.assertThat(sampleBuildingPage1.size()).isEqualTo(10);

        List<BuildingSearchResponseDto> sampleBuildingPage3 =
                this.buildingProfileService.searchBuilding("sample building", 3);
        Assertions.assertThat(sampleBuildingPage3.size()).isEqualTo(10);

        List<BuildingSearchResponseDto> sampleBuildingPage4 =
                this.buildingProfileService.searchBuilding("sample building", 4);
        Assertions.assertThat(sampleBuildingPage4).isEmpty();
    }
}
