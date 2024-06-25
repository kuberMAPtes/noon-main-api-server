package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedVotes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedVotesRepository extends JpaRepository<FeedVotes, Integer>  {
    // blank
}
