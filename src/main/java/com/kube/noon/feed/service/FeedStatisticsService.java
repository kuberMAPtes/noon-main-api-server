package com.kube.noon.feed.service;

import com.kube.noon.feed.dto.FeedCntByTagDto;
import com.kube.noon.feed.dto.FeedPopularityDto;
import com.kube.noon.feed.dto.FeedViewCntByBuildingDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeedStatisticsService {
    List<FeedViewCntByBuildingDto> getFeedViewCntByBuilding(int buildingId);

    List<FeedCntByTagDto> getFeedCntByTag();

    List<FeedPopularityDto> getFeedPopularity(int buildingId);
}
