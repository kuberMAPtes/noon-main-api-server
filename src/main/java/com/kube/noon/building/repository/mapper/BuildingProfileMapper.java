package com.kube.noon.building.repository.mapper;

import com.kube.noon.building.domain.Building;
import com.kube.noon.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

import java.util.List;



@Mapper
public interface BuildingProfileMapper {


    /**
     * 회원별 건물 구독 목록 보기
     * 특정 사용자 ID로 건물 구독 목록을 찾는다.
     *
     * @param memberId 본인의 구독 목록을 보려는 회원의 계정 ID
     * @return 구독한 건물 목록
     *
     * @author 허예지
     */

    List<Building> findBuildingSubscriptionListByMemberId(@Param("memberId") String memberId);


    /**
     * 건물별 구독자 목록 보기
     * Building ID로 특정 건물의 구독자 목록을 가져온다.
     *
     * @param buildingId 구독자 목록을 보려는 빌딩 ID
     * @return 구독자ID를 통해 조회한 members 레코드
     *
     * @author 허예지
     */
    public List<Member> findBuildingSubscriberListByBuildingId(@Param("buildingId")Integer buildingId);
}
