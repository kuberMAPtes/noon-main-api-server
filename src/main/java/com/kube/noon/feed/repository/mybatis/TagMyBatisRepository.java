package com.kube.noon.feed.repository.mybatis;

import com.kube.noon.feed.dto.TagDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMyBatisRepository {
    /**
     * (deprecated)
     * @param feedId
     * @return
     */
    @Deprecated
    List<TagDto> getTagByFeedId(int feedId);
}
