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
import java.util.Collections;
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
    private String writerProfile;
    private int buildingId;
    private String buildingName;
    private PublicRange publicRange;
    private String title;
    private String feedText;
    private Long viewCnt;
    private LocalDateTime writtenTime;
    private FeedCategory feedCategory;
    private boolean like;
    private int likeCount;
    private boolean bookmark;
    private int bookmarkCount;
    private int popularity;
    private boolean modified;
    private boolean mainActivate;
    private boolean activate;
    private List<FeedAttachmentDto> attachments;
    private List<FeedCommentDto> comments;
    private List<TagDto> tags;
    private List<TagFeedDto> tagFeeds;
    private List<String> updateTagList; // 피드를 추가할 때 피드 내용을 가져올 리스트

    public static FeedDto toDto(Feed feed) {
        // NullPointException
        Building building = feed.getBuilding();
        if(building == null) {
            building = Building.builder().build();
        }

        Member writer = feed.getWriter();
        if(writer == null) {
            writer = Member.builder().build();
        }

        return FeedDto.builder()
                .feedId(feed.getFeedId())
                .writerId(writer.getMemberId())
                .writerNickname(writer.getNickname())
                .writerProfile(writer.getProfilePhotoUrl())
                .buildingId(building.getBuildingId())
                .buildingName(building.getBuildingName())
                .publicRange(feed.getPublicRange())
                .title(feed.getTitle())
                .feedText(feed.getFeedText())
                .viewCnt(feed.getViewCnt())
                .writtenTime(feed.getWrittenTime())
                .feedCategory(feed.getFeedCategory())
                .modified(feed.isModified())
                .mainActivate(feed.isMainActivated())
                .activate(feed.isActivated())
                .attachments(
                        feed.getAttachments() == null ?
                                Collections.emptyList() :
                                feed.getAttachments().stream().map(FeedAttachmentDto::toDto).filter(s->s.isActivated()).collect(Collectors.toList())
                )
                .comments(
                        feed.getComments() == null ?
                                Collections.emptyList() :
                                feed.getComments().stream().map(FeedCommentDto::toDto).filter(s-> (s.isActivated())).collect(Collectors.toList())
                )
                .tagFeeds(
                        feed.getTagFeeds() == null ?
                                Collections.emptyList() :
                                feed.getTagFeeds().stream().map(TagFeedDto::toDto).collect(Collectors.toList())
                )
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
                .mainActivated(feedDto.isMainActivate())
                .activated(feedDto.isActivate())
                .attachments(
                        feedDto.getAttachments() == null ?
                                Collections.emptyList() :
                                feedDto.getAttachments().stream().map(FeedAttachmentDto::toEntity).collect(Collectors.toList())
                )
                .comments(
                        feedDto.getComments() == null ?
                                Collections.emptyList() :
                                feedDto.getComments().stream().map(FeedCommentDto::toEntity).collect(Collectors.toList())
                )
                .tagFeeds(
                        feedDto.getTagFeeds() == null ?
                                Collections.emptyList() :
                                feedDto.getTagFeeds().stream().map(TagFeedDto::toEntity).collect(Collectors.toList())
                )
                .build();
    }

    public static List<FeedDto> toDtoList(List<Feed> feedList) {
        return feedList.stream().map(FeedDto::toDto).collect(Collectors.toList());
    }
}
