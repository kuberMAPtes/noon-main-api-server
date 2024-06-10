package com.kube.noon.feed.repository.mybatis;

import com.kube.noon.feed.dto.FeedCntByTagDto;
import com.kube.noon.feed.dto.FeedPopularityDto;
import com.kube.noon.feed.dto.MemberLikeTagDto;
import com.kube.noon.feed.dto.FeedViewCntByBuildingDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMyBatisRepository {
    List<FeedViewCntByBuildingDto> getFeedViewCntByBuilding(int buildingId);

    List<FeedCntByTagDto> getFeedCntByTag();

    List<FeedPopularityDto> getFeedPopularity(int buildingId);

    List<MemberLikeTagDto> getMemberLikeTag();
}
