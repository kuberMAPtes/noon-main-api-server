package com.kube.noon.feed.dto;

import com.kube.noon.building.domain.Building;
import com.kube.noon.building.repository.BuildingProfileRepository;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedComment;
import com.kube.noon.feed.domain.TagFeed;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.repository.MemberRepository;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedDto {
    private int feedId;
    private String writerId;
    private String writerNickname;
    private int buildingId;
    private String buildingName;
    private PublicRange publicRange;
    private String title;
    private String feedText;
    private Long viewCnt;
    private LocalDateTime writtenTime;
    private FeedCategory feedCategory;
    private boolean isModified;
    private boolean isMainActivated;
    private List<FeedComment> comments;
    private List<TagFeed> tagFeeds;

    public static FeedDto toDto(Feed feed) {
        return FeedDto.builder()
                .feedId(feed.getFeedId())
                .writerId(feed.getWriter().getMemberId())
                .writerNickname(feed.getWriter().getNickname())
                .buildingId(feed.getBuilding().getBuildingId())
                .buildingName(feed.getBuilding().getBuildingName())
                .publicRange(feed.getPublicRange())
                .title(feed.getTitle())
                .feedText(feed.getFeedText())
                .viewCnt(feed.getViewCnt())
                .writtenTime(feed.getWrittenTime())
                .feedCategory(feed.getFeedCategory())
                .isModified(feed.isModified())
                .isMainActivated(feed.isMainActivated())
                .comments(feed.getComments())
                .tagFeeds(feed.getTagFeeds())
                .build();
    }

    public static Feed toEntity(FeedDto feedDto) {
        return Feed.builder()
                .feedId(feedDto.getFeedId())
                .writer(Member.builder().memberId(feedDto.getWriterId()).nickname(feedDto.getWriterNickname()).build())
                .building(Building.builder().buildingId(feedDto.getBuildingId()).buildingName(feedDto.getBuildingName()).build())
                .publicRange(feedDto.getPublicRange())
                .title(feedDto.getTitle())
                .feedText(feedDto.getFeedText())
                .viewCnt(feedDto.getViewCnt())
                .writtenTime(feedDto.getWrittenTime())
                .feedCategory(feedDto.getFeedCategory())
                .modified(feedDto.isModified())
                .mainActivated(feedDto.isMainActivated())
                .comments(feedDto.getComments())
                .tagFeeds(feedDto.getTagFeeds())
                .build();
    }

    public static List<FeedDto> toDtoList(List<Feed> feeds) {
        return feeds.stream().map(FeedDto::toDto).collect(Collectors.toList());
    }
}
