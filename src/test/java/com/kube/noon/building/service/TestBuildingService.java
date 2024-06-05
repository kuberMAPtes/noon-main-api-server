package com.kube.noon.building.service;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.dto.BuildingZzimDto;
import com.kube.noon.building.repository.BuildingSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@Slf4j
@SpringBootTest
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
    @DisplayName("건물아이디로 건물 피드 요약 보기")
    @Test
    void getFeedAISummary(){

        log.info("빌딩피드 요약내용={}", buildingProfileService.getFeedAISummary(10099));

    }
}
