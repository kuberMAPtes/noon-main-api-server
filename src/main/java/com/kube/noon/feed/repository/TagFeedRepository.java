package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import com.kube.noon.feed.domain.TagFeed;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TagFeedRepository extends JpaRepository<TagFeed, Long> {
    /**
     * 연관 테이블의 태그를 삭제한다.
     * @param tag
     * @return
     */
     int deleteByTagAndFeed(Tag tag, Feed feed);
}
