package com.kube.noon.feed.dto;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedAttachment;
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
public class FeedSummaryDto {
    private int feedId;
    private String writerId;
    private String writerNickname;
    private String writerProfile;
    private String title;
    private String feedText;
    private Long viewCnt;
    private int buildingId;
    private String buildingName;
    private LocalDateTime writtenTime;
    private int feedAttachmentId;
    private FeedCategory feedCategory;
    private boolean like;
    private boolean bookmark;
    private int likeCount;
    private int commentCount;
    private boolean mainActivated;
    private String recommendMember; // 알고리즘 추천 피드임을 밝힘
    
    public static FeedSummaryDto toDto(Feed feed) {
        
        // 유효한 첨부파일 걸러내기
        List<FeedAttachment> attachments = null;
        if (feed.getAttachments() != null && feed.getAttachments().size() > 0) {
            attachments = feed.getAttachments().stream()
                    .filter(s -> s.isActivated() == true)
                    .collect(Collectors.toList());

            for(FeedAttachment attachment : attachments) {
                System.out.println(attachment);
            }
        }

        //
        return FeedSummaryDto.builder()
                .feedId(feed.getFeedId())
                .writerId(feed.getWriter().getMemberId())
                .writerNickname(feed.getWriter().getNickname())
                .writerProfile(feed.getWriter().getProfilePhotoUrl())
                .title(feed.getTitle())
                .feedText(feed.getFeedText())
                .viewCnt(feed.getViewCnt())
                .buildingId(feed.getBuilding() == null ? 0 : feed.getBuilding().getBuildingId())
                .buildingName(feed.getBuilding() == null ? null : feed.getBuilding().getBuildingName())
                .writtenTime(feed.getWrittenTime())
                .feedCategory(feed.getFeedCategory())
                .recommendMember(feed.getRecommendMember())
                .mainActivated(feed.isMainActivated())
                .feedAttachmentId((attachments == null || attachments.isEmpty()) ? 0 : attachments.get(0).getAttachmentId())
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
