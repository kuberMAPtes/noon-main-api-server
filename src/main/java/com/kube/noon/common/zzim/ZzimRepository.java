package com.kube.noon.common.zzim;

import com.kube.noon.building.domain.Building;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface ZzimRepository extends JpaRepository<Zzim, Integer> {

    /**
     * 특정 사용자 ID로 건물 구독 목록을 찾는다.
     *
     * @param memberId 본인의 구독 목록을 보려는 회원의 계정 ID
     * @return 구독한 건물 목록
     *
     * @author 허예지
     */
    @Query(value = """
            SELECT *
            FROM building
            WHERE building_id IN (SELECT building_id
                    FROM zzim
                    WHERE zzim_type='SUBSCRIPTION' AND member_id = :memberId AND activated = 1)
            """, nativeQuery = true)
    public List<Building> findBuildingSubscriptionListByMemberId(@Param("memberId") String memberId);

}
