package com.kube.noon.common.zzim;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ZzimRepository extends JpaRepository<Zzim, Integer> {

    /**
     * 본인이 건물을 구독했던 History가 있는지 조회.
     * History가 있다면 재구독 할 때 activated만 변경한다.
     * History가 없다면 새 레코드를 INSERT한다.
     *
     * @param buildingId 구독History를 조회하려는 건물 아이디
     * @param memberId 구독History를 조회하려는 회원 아이디
     * @param subscriptionProviderId 본인이 구독했었는지, 타회원 구독 가져오기로 구독했었는지 구분하는 필드
     * @return 구독했던 이력이 있다면 true, 없다면 false
     *
     * @author 허예지
     *
     */

    boolean existsByBuildingIdAndMemberIdAndSubscriptionProviderId(int buildingId, String memberId, String subscriptionProviderId);


    /**
     * 회원이 구독했다 취소한 History가 있다면 그 레코드의 activate만 true로 update한다.
     *
     * @param activated true이면 구독, false이면 구독취소
     *
     * @author 허예지
     */
    @Modifying
    @Transactional
    @Query("UPDATE Zzim SET activated = :activated WHERE buildingId = :buildingId AND memberId = :memberId")
    void updateZzimActivated(@Param("buildingId") int buildingId, @Param("memberId") String memberId, @Param("activated") boolean activated);

    /**
     * 빌딩아이디, 구독자아이디, 구독제공자아이디로 Zzim레코드를 조회한다.
     * 
     * @return Zzim엔티티
     *
     * @author 허예지
     */
    Zzim findByBuildingIdAndMemberId(int buildingId, String memberId);
}
