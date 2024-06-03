package com.kube.noon.building.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.dto.BuildingDto;
import com.kube.noon.building.repository.mapper.BuildingProfileMapper;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

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
            log.info("회원비밀번호={}", member.getPassword());
            log.info("프로필사진경로={}", member.getProfilePhotoUrl());
            log.info("닉네임={}", member.getNickname());
            log.info("다정수치={}", member.getDajungScore());
            log.info("전화번호={}", member.getPhoneNumber());
            log.info("프로필소개={}", member.getProfileIntro());

        }

    }
}

