package com.kube.noon.building.repository;

import com.kube.noon.building.domain.Building;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class TestBuildingProfileRepository {

    @Autowired
    BuildingProfileRepository buildingProfileRepository;

    @Autowired
    BuildingZzimRepository buildingZzimRepository;

    @DisplayName("빌딩아이디로 빌딩 정보가져오기")
    @Test
    void findBuildingProfileByBuildingId() {
        Building result = this.buildingProfileRepository.findBuildingProfileByBuildingId(10009);

        log.info("빌딩이름={}", result.getBuildingName());
        log.info("빌딩경도={}", result.getLongitude());
        log.info("빌딩위도={}", result.getLatitude());
        log.info("빌딩피드요약={}", result.getFeedAiSummary());
        log.info("빌딩도로명주소={}", result.getRoadAddr());

    }


}

