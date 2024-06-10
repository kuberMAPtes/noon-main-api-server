package com.kube.noon.feed.service.impl;

import com.kube.noon.feed.dto.FeedCntByTagDto;
import com.kube.noon.feed.dto.FeedPopularityDto;
import com.kube.noon.feed.dto.FeedViewCntByBuildingDto;
import com.kube.noon.feed.dto.MemberLikeTagDto;
import com.kube.noon.feed.repository.mybatis.FeedMyBatisRepository;
import com.kube.noon.feed.service.FeedStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedStatisticsServiceImpl implements FeedStatisticsService {

    @Autowired
    private FeedMyBatisRepository feedMyBatisRepository;

    @Override
    public List<FeedViewCntByBuildingDto> getFeedViewCntByBuilding(int buildingId) {
        return feedMyBatisRepository.getFeedViewCntByBuilding(buildingId);
    }

    @Override
    public List<FeedCntByTagDto> getFeedCntByTag() {
        return feedMyBatisRepository.getFeedCntByTag();
    }

    @Override
    public List<FeedPopularityDto> getFeedPopularity(int buildingId) {
        return feedMyBatisRepository.getFeedPopularity(buildingId);
    }

    @Override
    public List<MemberLikeTagDto> getMemberLikeTag() {
        return feedMyBatisRepository.getMemberLikeTag();
    }
}
