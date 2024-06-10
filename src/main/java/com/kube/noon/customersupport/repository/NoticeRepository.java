package com.kube.noon.customersupport.repository;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.customersupport.domain.Report;
import com.kube.noon.feed.domain.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Feed, Integer> {

    List<Feed> findByFeedCategoryAndActivated(FeedCategory feedCategory, boolean activated);
    Page<Feed> findByFeedCategoryAndActivated(FeedCategory feedCategory, boolean activated, Pageable pageable);

}
