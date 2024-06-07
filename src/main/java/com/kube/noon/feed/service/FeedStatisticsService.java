package com.kube.noon.feed.service;

import com.kube.noon.feed.dto.FeedCntByTagDto;
import com.kube.noon.feed.dto.FeedPopularityDto;
import com.kube.noon.feed.dto.FeedViewCntByBuildingDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeedStatisticsService {
    // 건물별로 조회수가 높은 피드를 5개 가져온다.
    List<FeedViewCntByBuildingDto> getFeedViewCntByBuilding(int buildingId);

    // 많이 쓰이는 태그 중 상위 5개를 가져온다.
    List<FeedCntByTagDto> getFeedCntByTag();

    // 건물별로 인기가 높은 피드 5개를 가져온다.
    List<FeedPopularityDto> getFeedPopularity(int buildingId);
}