package com.kube.noon.building.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.common.zzim.ZzimRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class TestBuildingProfileRepository {

    @Autowired
    BuildingProfileRepository buildingProfileRepository;

    @Autowired
    ZzimRepository zzimRepository;

    @DisplayName("빌딩아이디로 빌딩 정보가져오기")
    @Test
    void findBuildingProfileByBuildingId() {
        Building result = buildingProfileRepository.findBuildingProfileByBuildingId(10009);

        log.info("빌딩이름={}", result.getBuildingName());
        log.info("빌딩경도={}", result.getLongitude());
        log.info("빌딩위도={}", result.getLatitude());
        log.info("빌딩피드요약={}", result.getFeedAiSummary());
        log.info("빌딩도로명주소={}", result.getRoadAddr());

    }

    @DisplayName("멤버아이디로 회원별 구독 건물 정보가져오기")
    //@Test
    void findUserBuildingSubscriptionListByMemberId() {
        List<Building> results = zzimRepository.findBuildingSubscriptionListByMemberId("member_996");

        for (Building result : results) {

            log.info("빌딩이름={}", result.getBuildingName());
            log.info("빌딩경도={}", result.getLongitude());
            log.info("빌딩위도={}", result.getLatitude());
            log.info("빌딩피드요약={}", result.getFeedAiSummary());
            log.info("빌딩도로명주소={}", result.getRoadAddr());


        }

    }
}

