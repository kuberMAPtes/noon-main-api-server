package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedComment;
import com.kube.noon.member.domain.Member;
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
public class FeedCommentDto {
    private int feedId;
    private int commentId;
    private String memberId;
    private String commentText;
    private LocalDateTime writtenTime;
    private boolean activated;

    public static FeedCommentDto toDto(FeedComment feedComment) {
        return FeedCommentDto.builder()
                .feedId(feedComment.getFeed().getFeedId())
                .commentId(feedComment.getCommentId())
                .memberId(feedComment.getMember().getMemberId())
                .commentText(feedComment.getCommentText())
                .writtenTime(feedComment.getWrittenTime())
                .build();
    }

    public static FeedComment toEntity(FeedCommentDto feedCommentDto) {
        return FeedComment.builder()
                .feed(Feed.builder().feedId(feedCommentDto.getFeedId()).build())
                .commentId(feedCommentDto.getCommentId())
                .member(Member.builder().memberId(feedCommentDto.getMemberId()).build())
                .commentText(feedCommentDto.getCommentText())
                .writtenTime(feedCommentDto.getWrittenTime())
                .build();
    }

    public static List<FeedCommentDto> toDtoList(List<FeedComment> feedCommentList) {
        return feedCommentList.stream().map(FeedCommentDto::toDto).collect(Collectors.toList());
    }
}
