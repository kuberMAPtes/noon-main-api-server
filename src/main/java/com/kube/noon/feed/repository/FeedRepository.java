package com.kube.noon.feed.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    /**
     * 하나에 피드에 대한 상세보기를 제공한다.
     *
     * @param feedId : PK인 feedId를 받는다.
     * @return Feed : Feed repository를 반환받는다.
     */
    Feed findByFeedId(int feedId);

    /**
     * 전체 저장된 피드를 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @return List<Feed>
     */
    List<Feed> findByActivatedTrue();

    List<Feed> findByActivatedTrue(Pageable pageable);

    /**
     * 회원별로 작성한 피드를 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @param writer 회원ID를 받는다.
     * @return List<Feed>
     */
    List<Feed> findByWriterAndActivatedTrue(Member writer);

    List<Feed> findByWriterAndActivatedTrue(Member writer, Pageable pageable);

    /**
     * 건물별로 작성된 피드를 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @return List<Feed>
     */
    List<Feed> findByBuildingAndActivatedTrue(Building building);

    List<Feed> findByBuildingAndActivatedTrue(Building building, Pageable pageable);

    /**
     * 건물별 피드 중 카테고리에 알맞는 피드를 가져온다 : 주사용처 -> 확성기
     */
    List<Feed> findByBuildingAndFeedCategoryAndActivatedTrue(Building building, FeedCategory feedCategory);

    /**
     * 회원의 메인 피드를 가져온다.
     *
     * @param writer 대상 회원을 받는다.
     * @return Feed 회원의 메인 피드를 가져온다.
     */
    List<Feed> findByWriterAndMainActivatedTrue(Member writer);

    List<Feed> findByWriterAndMainActivatedTrue(Member writer, Pageable pageable);

    /**
     * 회원이 좋아요를 누른 피드 목록을 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @param member Member 객체를 받는다.
     * @return List<Feed>
     */
    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.feedId = z.feedId 
            WHERE z.zzimType = 'LIKE' AND z.memberId = :#{#member.memberId} AND f.activated = true AND z.activated = true
           """)
    List<Feed> findByMemberLikeFeed(@Param("member") Member member);

    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.feedId = z.feedId 
            WHERE z.zzimType = 'LIKE' AND z.memberId = :#{#member.memberId} AND f.activated = true AND z.activated = true
           """)
    List<Feed> findByMemberLikeFeed(@Param("member") Member member, Pageable pageable);

    /**
     * 하나의 빌딩 내에서 회원이 좋아요를 누른 피드 목록을 가져온다.
     *
     * @param member   멤버의 아이디를 가져온다
     * @param building 빌딩 번호를 가져온다.
     * @return 피드 목록 결과를 가져온다.
     */
    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.feedId = z.feedId 
            WHERE z.zzimType = 'LIKE' 
            AND z.memberId = :#{#member.memberId} 
            AND f.building = :#{#building}
            AND f.activated = true 
            AND z.activated = true
          """)
    List<Feed> findByMemberAndBuildingIdLikeFeed(@Param("member") Member member, @Param("building") Building building);

    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.feedId = z.feedId 
            WHERE z.zzimType = 'LIKE' 
            AND z.memberId = :#{#member.memberId} 
            AND f.building = :#{#building}
            AND f.activated = true 
            AND z.activated = true
          """)
    List<Feed> findByMemberAndBuildingIdLikeFeed(@Param("member") Member member, @Param("building") Building building, Pageable pageable);

    /**
     * 회원이 북마크를 한 피드 목록을 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @param member Member 객체를 받는다.
     * @return List<Feed>
     */
    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.feedId = z.feedId 
            WHERE z.zzimType = 'BOOKMARK' AND z.memberId = :#{#member.memberId} AND f.activated = true AND z.activated = true 
           """)
    List<Feed> findByMemberBookmarkFeed(@Param("member") Member member);

    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.feedId = z.feedId 
            WHERE z.zzimType = 'BOOKMARK' AND z.memberId = :#{#member.memberId} AND f.activated = true AND z.activated = true 
           """)
    List<Feed> findByMemberBookmarkFeed(@Param("member") Member member, Pageable pageable);

    /**
     * 회원이 구독한 건물에 대한 피드 목록을 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @param member Member 객체를 받는다.
     * @return List<Feed>
     */
    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.building.buildingId = z.buildingId 
            WHERE z.zzimType = 'SUBSCRIPTION' AND z.memberId = :#{#member.memberId} AND f.activated = true AND z.activated = true
           """)
    List<Feed> findByMemberBuildingSubscription(@Param("member") Member member);

    @Query("""
            SELECT f FROM Feed f 
            INNER JOIN Zzim z ON f.building.buildingId = z.buildingId 
            WHERE z.zzimType = 'SUBSCRIPTION' AND z.memberId = :#{#member.memberId} AND f.activated = true AND z.activated = true
           """)
    List<Feed> findByMemberBuildingSubscription(@Param("member") Member member, Pageable pageable);

    /**
     * 피드에 좋아요를 누른 맴버의 목록을 가져옵니다.
     *
     * @param feed
     * @return List<Member>
     */
    @Query("""
            SELECT m FROM Member m 
            INNER JOIN Zzim z ON m.memberId = z.memberId 
            WHERE z.feedId = :#{#feed.feedId} AND z.zzimType = 'LIKE' AND z.activated = true
           """)
    List<Member> getFeedLikeList(@Param("feed") Feed feed);

    /**
     * 빌딩 내 건물 중 member가 좋아요를 누른 내용을 우선 정렬한다.
     *
     * @param member   좋아요를 누른 회원
     * @param building 건물 번호
     * @return
     */
    @Query(value = """
            SELECT f.*
            FROM feed f
            LEFT JOIN (
                SELECT z.feed_id
                FROM zzim z
                WHERE z.zzim_type = 'LIKE'
                  AND z.member_id = :#{#member.memberId}
                  AND z.activated = true
            ) liked_feeds
            ON f.feed_id = liked_feeds.feed_id
            WHERE f.building_id = :#{#building.buildingId}
              AND f.activated = true
            ORDER BY liked_feeds.feed_id IS NULL, f.written_time;
           """, nativeQuery = true)
    List<Feed> findFeedWithLikesFirst(@Param("member") Member member, @Param("building") Building building);

    @Query(value = """
            SELECT f.*
            FROM feed f
            LEFT JOIN (
                SELECT DISTINCT z.feed_id
                FROM zzim z
                WHERE z.zzim_type = 'LIKE'
                  AND z.member_id = :#{#member.memberId}
                  AND z.activated = true
            ) liked_feeds
            ON f.feed_id = liked_feeds.feed_id
            WHERE f.building_id = :#{#building.buildingId}
              AND f.activated = true
            ORDER BY liked_feeds.feed_id IS NULL, f.written_time;
           """, nativeQuery = true)
    List<Feed> findFeedWithLikesFirst(@Param("member") Member member, @Param("building") Building building, Pageable pageable);

    /**
     * 제목이나 내용을 통해 피드를 검색할 수 있도록 합니다.
     *
     * @param keyword 검색할 keyword를 입력합니다.
     * @return List<Feed>
     */
    List<Feed> findByFeedTextContainingIgnoreCase(String keyword);

    List<Feed> findByTitleContainingIgnoreCase(String keyword);

    /**
     * 제목이나 내용을 통해 피드를 검색할 수 있도록 합니다.
     * 이 메서드를 이용하여 통합 검색과 페이징이 가능합니다.
     *
     * @param keyword
     * @param pageable
     */
    @Query("SELECT f FROM Feed f WHERE f.title LIKE %:#{#keyword}% OR f.feedText LIKE %:#{#keyword}%")
    List<Feed> searchFeedByKeyword(String keyword, Pageable pageable);

    /**
     * 가장 최근에 추가한  feedId를 가져온다.
     *
     * @return FeedId
     */
    @Query("SELECT MAX(fa.feedId) FROM Feed fa")
    Integer findMaxId();

    /**
     *
     */
}
