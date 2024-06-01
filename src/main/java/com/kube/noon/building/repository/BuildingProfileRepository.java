package com.kube.noon.building.repository;

import com.kube.noon.building.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingProfileRepository extends JpaRepository<Building, Integer> {

    Building findBuildingProfileByBuildingId(int buildingId);

}
