package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    /**
     * 피드의 댓글 리스트를 가져온다.
     * @param feed
     * @return
     */
    List<FeedComment> findByFeed(Feed feed);

    /**
     * 댓글 아이디로 댓글을 가져온다.
     * @param commentId
     * @return
     */
    FeedComment findByCommentId(int commentId);
}
