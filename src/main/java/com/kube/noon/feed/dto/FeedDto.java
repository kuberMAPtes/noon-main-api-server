package com.kube.noon.feed.dto;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import com.kube.noon.feed.entity.FeedComment;
import com.kube.noon.feed.entity.TagFeed;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedDto {
    private int feedId;
    private String writerId;
    private int buildingId;
    private boolean mainActivated;
    private PublicRange publicRange;
    private String title;
    private String feedText;
    private Long viewCnt;
    private LocalDateTime writtenTime;
    private FeedCategory feedCategory;
    private boolean modified;
    private boolean activated;
    private List<FeedComment> comments;
    private List<TagFeed> tagFeeds;
}
