package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Feed findByFeedId(int feedId);
    List<Feed> findAllByActivated(Boolean activated);
}
