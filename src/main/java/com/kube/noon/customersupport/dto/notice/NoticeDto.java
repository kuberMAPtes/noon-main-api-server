package com.kube.noon.customersupport.dto.notice;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.feed.domain.Feed;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 클라이언트에 공지 내용만 보여줄 때 활용하는 Dto
 *
 */
@Data
@Builder
public class NoticeDto {


    private int feedId;
    private String title;
    private String feedText;
    private Long viewCnt;
    private LocalDateTime writtenTime;
    private FeedCategory feedCategory;
    private boolean activated;
    private String writerId;


    public static NoticeDto fromEntity(Feed feed) {
        return NoticeDto.builder()
                .feedId(feed.getFeedId())
                .title(feed.getTitle())
                .feedText(feed.getFeedText())
                .viewCnt(feed.getViewCnt())
                .writtenTime(feed.getWrittenTime())
                .feedCategory(FeedCategory.NOTICE)
                .activated(feed.isActivated())
                .writerId(feed.getWriter().getMemberId())
                .build();
    }


}
