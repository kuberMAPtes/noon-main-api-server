package com.kube.noon.common.zzim;

import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ZzimRepository extends JpaRepository<Zzim, Integer> {

    /**
     * 본인이 건물을 구독했던 History가 있는지 조회.
     * History가 있다면 재구독 할 때 activated만 변경한다.
     * History가 없다면 새 레코드를 INSERT한다.
     *
     * @param buildingId             구독History를 조회하려는 건물 아이디
     * @param memberId               구독History를 조회하려는 회원 아이디
     * @param subscriptionProviderId 본인이 구독했었는지, 타회원 구독 가져오기로 구독했었는지 구분하는 필드
     * @return 구독했던 이력이 있다면 true, 없다면 false
     * @author 허예지
     */
    boolean existsByBuildingIdAndMemberIdAndSubscriptionProviderId(int buildingId, String memberId, String subscriptionProviderId);


    /**
     * 회원이 구독했다 취소한 History가 있다면 그 레코드의 activate만 true로 update한다.
     *
     * @param activated true이면 구독, false이면 구독취소
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
     * @author 허예지
     */
    List<Zzim> findByBuildingIdAndMemberIdAndActivated(int buildingId, String memberId, boolean activated);


    /**
     * 구독자 수 조회
     *
     * @param buildingId 구독자 수를 조회하려는 건물 아이디
     * @param activated  입력은 true. 구독중인 구독자만 조회한다.
     * @return
     */
    int countByBuildingIdAndActivated(int buildingId, boolean activated);


    /**
     * 건물 아이디로 구독자 목록 조회
     *
     * @param buildingId 구독자를 조회하려는 건물아이디
     * @return 구독자 아이디 목록
     */
    @Query("SELECT zzim.memberId FROM Zzim zzim WHERE zzim.buildingId = :buildingId AND zzim.activated = TRUE ")
    List<String> findMemberIdsByBuildingId(int buildingId);


    /**
     * 피드 아이디, 유저 아이디, 찜 타입을 통해 Zzim Table의 데이터를 확인한다.
     *
     * @param feedId
     * @param memberId
     * @param zzimType
     * @return Zzim 해당하는 Zzim 하나를 가져온다.
     */
    List<Zzim> findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(int feedId, String memberId, ZzimType zzimType);

    /**
     * 회원 아이디와 찜 타입을 통해 회원의 좋아요, 북마크 여부를 확인한다.
     *
     * @param memberId
     * @param zzimType
     * @return
     */
    @Query("SELECT z.feedId FROM Zzim z WHERE z.memberId = :#{#memberId} AND z.zzimType = :#{#zzimType} AND z.activated = true")
    List<Integer> getFeedIdByMemberIdAndZzimType(String memberId, ZzimType zzimType);

    /**
     * zzimType에 맞는 개수를 가지고 온다.
     * 예시 : 피드 당 좋아요 개수
     */
    @Query("SELECT COUNT(z) FROM Zzim z WHERE z.feedId = :#{#feedId} AND z.zzimType = :#{#zzimType} AND z.activated = true")
    int getCountByFeedIdZzimType(int feedId, ZzimType zzimType);
}
