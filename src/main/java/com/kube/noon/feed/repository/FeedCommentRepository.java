package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    List<FeedComment> findByFeed(Feed feed);

    FeedComment findByCommentId(int commentId);
}
