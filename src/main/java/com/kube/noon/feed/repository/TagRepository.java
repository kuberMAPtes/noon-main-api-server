package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByTagText(String tagText);
}
