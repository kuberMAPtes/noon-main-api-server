package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Tag;
import com.kube.noon.feed.domain.TagFeed;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TagFeedRepository extends JpaRepository<TagFeed, Long> {
    int deleteByTag(Tag tag);
}
