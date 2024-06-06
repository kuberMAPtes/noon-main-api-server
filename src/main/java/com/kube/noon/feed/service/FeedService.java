package com.kube.noon.feed.service;

import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.dto.FeedSummaryDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface FeedService {
    List<FeedSummaryDto> getFeedListByMember(String memberId);

    List<FeedSummaryDto> getFeedListByBuilding(int buildingId);

    List<FeedSummaryDto> getFeedListByMemberLike(String memberId);

    List<FeedSummaryDto> getFeedListByMemberBookmark(String memberId);

    List<FeedSummaryDto> getFeedListByBuildingSubscription(String memberId);

    int addFeed(FeedDto feedDto);

    int updateFeed(FeedDto feedSummaryDto);

    int deleteFeed(FeedDto feedSummaryDto);

    FeedDto getFeedById(int feedId);

    int setPublicRage(FeedDto feedDto);

    int setMainFeed(FeedDto feedDto);

    List<FeedSummaryDto> searchFeedList(String keyword);
}
