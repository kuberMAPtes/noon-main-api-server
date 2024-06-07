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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

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
}
