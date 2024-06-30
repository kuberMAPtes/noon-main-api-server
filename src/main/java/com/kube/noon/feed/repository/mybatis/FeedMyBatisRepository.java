package com.kube.noon.feed.repository.mybatis;

import com.kube.noon.feed.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedMyBatisRepository {
    List<FeedViewCntByBuildingDto> getFeedViewCntByBuilding(int buildingId);

    List<FeedCntByTagDto> getFeedCntByTag();

    List<FeedPopularityDto> getFeedPopularity(int buildingId);

    List<MemberLikeTagDto> getMemberLikeTag();

    List<FeedPopularityDto> getAllFeedOrderByPopolarity(@Param("limit") int limit, @Param("offset") int offset);
}
