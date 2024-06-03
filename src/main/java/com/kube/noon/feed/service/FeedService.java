package com.kube.noon.feed.service;

import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.entity.Feed;

import java.io.IOException;
import java.util.List;

public interface FeedService {
    public List<Feed> getFeedList() throws IOException;

    public default Feed dtoToEntity(FeedDto feedDto) {
        Feed feed = Feed.builder()
                .feedId(feedDto.getFeedId())
                .title(feedDto.getTitle())
                .writerId(feedDto.getWriterId())
                .buildingId(feedDto.getBuildingId())
                .mainActivated(feedDto.isMainActivated())
                .publicRange(feedDto.getPublicRange())
                .feedText(feedDto.getFeedText())
                .viewCnt(feedDto.getViewCnt())
                .feedCategory(feedDto.getFeedCategory())
                .modified(feedDto.isModified())
                .activated(feedDto.isActivated())
                .comments(feedDto.getComments())
                .tagFeeds(feedDto.getTagFeeds())
                .build();

        return feed;
    }

    public default FeedDto entityToDto(Feed feed) {
        FeedDto feedDto = FeedDto.builder()
                .feedId(feed.getFeedId())
                .writerId(feed.getWriterId())
                .buildingId(feed.getBuildingId())
                .mainActivated(feed.isMainActivated())
                .publicRange(feed.getPublicRange())
                .title(feed.getTitle())
                .feedText(feed.getFeedText())
                .viewCnt(feed.getViewCnt())
                .writtenTime(feed.getWrittenTime())
                .feedCategory(feed.getFeedCategory())
                .modified(feed.isModified())
                .activated(feed.isActivated())
                .comments(feed.getComments())
                .tagFeeds(feed.getTagFeeds())
                .build();
        return feedDto;
    }
}
