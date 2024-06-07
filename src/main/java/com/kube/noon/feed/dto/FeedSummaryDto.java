package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedSummaryDto {
    private int feedId;
    private String writerId;
    private String writerNickname;
    private String title;
    private String feedText;
    private int buildingId;
    private String buildingName;
    private String feedAttachementURL;

    public static FeedSummaryDto toDto(Feed feed) {
        return FeedSummaryDto.builder()
                .feedId(feed.getFeedId())
                .writerId(feed.getWriter().getMemberId())
                .writerNickname(feed.getWriter().getNickname())
                .title(feed.getTitle())
                .feedText(feed.getFeedText())
                .buildingId(feed.getBuilding().getBuildingId())
                .buildingName(feed.getBuilding().getBuildingName())
                .feedAttachementURL((feed.getAttachments() == null || feed.getAttachments().size() == 0) ? null : feed.getAttachments().get(0).getFileUrl())
                .build();
    }

    public static List<FeedSummaryDto> toDtoList(List<Feed> feeds) {
        if(feeds == null || feeds.isEmpty()) {
            return null;
        } else {
            return feeds.stream().map(FeedSummaryDto::toDto).collect(Collectors.toList());
        }
    }
}
