package com.kube.noon.building.service;
import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.repository.mapper.BuildingProfileMapper;
import com.kube.noon.common.zzim.Zzim;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class TestBuildingService {

    @Autowired
    @Qualifier("buildingProfileServiceImpl")
    BuildingProfileService buildingProfileService;


    @DisplayName("멤버아이디, 건물아이디로 구독하기")
    @Test
    void addSubscription() {

        Zzim zzim = buildingProfileService.addSubscription("member_2",10000);

        log.info("건물아이디={}", zzim.getBuildingId());
        log.info("찜타입={}", zzim.getZzimType());
        log.info("구독자아이디={}", zzim.getMemberId());
        log.info("구독제공자아이디={}", zzim.getSubscriptionProviderId());
        log.info("찜아이디={}", zzim.getZzimId());
        log.info("찜activated={}", zzim.isActivated());

    }

    @DisplayName("멤버아이디, 건물아이디로 구독 취소하기")
    @Test
    void deleteSubscription() {

        Zzim zzim = buildingProfileService.deleteSubscription("member_2",10000);

        log.info("건물아이디={}", zzim.getBuildingId());
        log.info("찜타입={}", zzim.getZzimType());
        log.info("구독자아이디={}", zzim.getMemberId());
        log.info("구독제공자아이디={}", zzim.getSubscriptionProviderId());
        log.info("찜아이디={}", zzim.getZzimId());
        log.info("찜activated={}", zzim.isActivated());

    }

    @DisplayName("멤버아이디와 타멤버아이디로 타회원 구독 목록 가져오기")
    @Test
    void addSubscriptionFromSomeone() {

        List<Building> buildings = buildingProfileService.addSubscriptionFromSomeone("member_3", "member_1");
        List<BuildingDto> buildingDtos = buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());

        for (BuildingDto buildingDto : buildingDtos) {

            log.info("빌딩이름={}", buildingDto.getBuildingName());
            log.info("빌딩도로명주소={}", buildingDto.getRoadAddr());

        }

    }
}
