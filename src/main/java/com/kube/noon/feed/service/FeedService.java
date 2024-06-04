package com.kube.noon.feed.service;

import com.kube.noon.building.domain.Building;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.member.domain.Member;

import java.io.IOException;
import java.util.List;

public interface FeedService {
    public List<Feed> getFeedList() throws IOException;

    public default Feed dtoToEntity(FeedDto feedDto) {
        // 임시로 생성 후 대입
        Member writer = new Member();
        writer.setMemberId(feedDto.getWriterId());

        Feed feed = Feed.builder()
                .feedId(feedDto.getFeedId())
                .title(feedDto.getTitle())
                .mainActivated(feedDto.isMainActivated())
                .publicRange(feedDto.getPublicRange())
                .feedText(feedDto.getFeedText())
                .viewCnt(feedDto.getViewCnt())
                .feedCategory(feedDto.getFeedCategory())
                .modified(feedDto.isModified())
                .activated(feedDto.isActivated())
                .comments(feedDto.getComments())
                .tagFeeds(feedDto.getTagFeeds())
                .building(Building.builder().buildingId(feedDto.getBuildingId()).build())
                .writer(writer)
                .build();

        return feed;
    }

    public default FeedDto entityToDto(Feed feed) {
        FeedDto feedDto = FeedDto.builder()
                .feedId(feed.getFeedId())
                .writerId(feed.getWriter().getMemberId())
                .buildingId(feed.getBuilding().getBuildingId())
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
