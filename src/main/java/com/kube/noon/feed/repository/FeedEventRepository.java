package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.FeedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedEventRepository extends JpaRepository<FeedEvent, Integer> {

    /**
     * 이벤트 하나를 찾는다.
     * @param feedId
     * @return
     */
    FeedEvent findByFeedId(int feedId);

    List<FeedEvent> findAll();
}
