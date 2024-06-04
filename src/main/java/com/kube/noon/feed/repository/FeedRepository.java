package com.kube.noon.feed.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.member.domain.Member;
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

    /**
     * 회원별로 작성한 피드를 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @param writer 회원ID를 받는다.
     * @return List<Feed>
     */
    List<Feed> findByWriterAndActivatedTrue(Member writer);

    /**
     * 건물별로 작성된 피드를 가져온다. 단, 활성화가 된 피드만 가져온다.
     *
     * @return List<Feed>
     */
    List<Feed> findByBuildingAndActivatedTrue(Building building);

    /**
     * 회원의 메인 피드를 가져온다.
     *
     * @param writer 대상 회원을 받는다.
     * @return Feed 회원의 메인 피드를 가져온다.
     */
    Feed findByWriterAndMainActivatedTrue(Member writer);

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

    /**
     * 피드에 좋아요를 누른 맴버의 목록을 가져옵니다.
     *
     * @param feed
     * @return List<Member>
     */
    @Query("""
            SELECT m FROM Member m 
            INNER JOIN Zzim z ON m.memberId = z.memberId 
            WHERE z.feedId = :#{#feed.feedId} AND z.zzimType = 'LIKE'
           """)
    List<Member> getFeedLikeList(@Param("feed") Feed feed);

    /**
     * 제목이나 내용을 통해 피드를 검색할 수 있도록 합니다.
     * @param keyword 검색할 keyword를 입력합니다.
     * @return List<Feed>
     */
    List<Feed> findByFeedTextContainingIgnoreCase(String keyword);

    List<Feed> findByTitleContainingIgnoreCase(String keyword);
}
