package com.kube.noon.building.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.repository.mapper.BuildingProfileMapper;
import com.kube.noon.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class TestBuildingProfileRepository {

    @Autowired
    BuildingProfileRepository buildingProfileRepository;


    @Autowired
    BuildingProfileMapper buildingProfileMapper;


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
    @Test
    void findUserBuildingSubscriptionListByMemberId() {

        List<Building> buildings = buildingProfileMapper.findBuildingSubscriptionListByMemberId("member_996");
        List<BuildingDto> buildingDtos = buildings.stream()
                .map(BuildingDto::fromEntity)
                .collect(Collectors.toList());

        for (BuildingDto buildingDto : buildingDtos) {

            log.info("빌딩이름={}", buildingDto.getBuildingName());
            log.info("빌딩경도={}", buildingDto.getLongitude());
            log.info("빌딩위도={}", buildingDto.getLatitude());
            log.info("빌딩피드요약={}", buildingDto.getFeedAiSummary());
            log.info("빌딩도로명주소={}", buildingDto.getRoadAddr());


        }

    }

    @DisplayName("건물아이디로 건물의 구독자 목록 가져오기")
    @Test
    void findBuildingSubscriberListByBuildingId() {

        List<Member> members = buildingProfileMapper.findBuildingSubscriberListByBuildingId(11513);

        for (Member member : members) {

            log.info("회원아이디={}", member.getMemberId());
            log.info("회원비밀번호={}", member.getPwd());
            log.info("프로필사진경로={}", member.getProfilePhotoUrl());
            log.info("닉네임={}", member.getNickname());
            log.info("다정수치={}", member.getDajungScore());
            log.info("전화번호={}", member.getPhoneNumber());
            log.info("프로필소개={}", member.getProfileIntro());

        }

    }

    @Transactional
    @Test
    void findBuildingByRoadAddr() {
        Building building = Building.builder()
                .buildingName("sample-building")
                .roadAddr("서울시 영등포구 영등포로 101")
                .profileActivated(true)
                .latitude(35.1425114)
                .longitude(127.34151365)
                .feedAiSummary("Hello Summary")
                .build();
        this.buildingProfileRepository.save(building);

        log.trace("building={}", building);

        Building findBuilding = this.buildingProfileRepository.findBuildingProfileByRoadAddr("서울시 영등포구 영등포로 101");

        log.trace("findBuilding={}", findBuilding);

        assertThat(findBuilding.getBuildingId()).isEqualTo(building.getBuildingId());
        assertThat(findBuilding.getBuildingName()).isEqualTo(building.getBuildingName());
        assertThat(findBuilding.getFeedAiSummary()).isEqualTo(building.getFeedAiSummary());
        assertThat(findBuilding.getRoadAddr()).isEqualTo(building.getRoadAddr());
    }
}

