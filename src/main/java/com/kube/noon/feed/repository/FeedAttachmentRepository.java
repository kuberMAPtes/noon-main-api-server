package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedAttachmentRepository extends JpaRepository<FeedAttachment, Long> {
    List<FeedAttachment> findByFeed(Feed feed);

    FeedAttachment findByAttachmentId(int attachmentId);
}
