package com.kube.noon.building.repository;

import com.kube.noon.building.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingProfileRepository extends JpaRepository<Building, Integer> {


    /**
     * 건물 프로필 보기
     * Building ID로 특정 건물의 정보를 가져온다.
     *
     * @param buildingId 건물 정보를 보고자 하는 건물의 ID
     * @return Building 정보
     *
     * @author 허예지
     */
    Building findBuildingProfileByBuildingId(int buildingId);

    @Query("SELECT b FROM Building b WHERE b.profileActivated = true")
    List<Building> findActivatedBuildings();




}
