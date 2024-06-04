package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedAttachmentRepository extends JpaRepository<FeedAttachment, Long> {
    /**
     * 피드의 첨부파일을 가져온다.
     * @param feed
     * @return
     */
    List<FeedAttachment> findByFeed(Feed feed);

    /**
     * 첨부파일 아이디로 첨부파일을 찾는다.
     * @param attachmentId
     * @return
     */
    FeedAttachment findByAttachmentId(int attachmentId);
}
