package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedCommentDto {
    private int commentId;
    private String comment;
    private String commentText;
    private LocalDateTime writtenTime;
    private boolean activated;
    private Feed feed;
}
