package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import com.kube.noon.feed.domain.TagFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TagFeedRepository extends JpaRepository<TagFeed, Long> {
    /**
     * 연관 테이블의 태그를 삭제한다.
     * @param tag
     * @return
     */
     int deleteByTagAndFeed(Tag tag, Feed feed);

    /**
     * 피드 번호에 속하는 연관 테이블의 모든 태그를 삭제한다.
     * 피드가 추가되거나 수정될 때 사용한다.
     * @param feed feedId가 담긴 객체를 받는다.
     * @return
     */
    @Modifying
    @Query("DELETE FROM TagFeed tf WHERE tf.feed.feedId = :#{#feed.feedId}")
    int deleteByFeed(@Param("feed") Feed feed);
}
