package com.kube.noon.feed.dto;

import com.kube.noon.building.domain.Building;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.member.domain.Member;
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
public class FeedMegaphoneDto {
    private int feedId;
    private String writerId;
    private String writerNickname;
    private String feedText;
    private LocalDateTime writtenTime;

    public static FeedMegaphoneDto toDto(Feed feed) {
        // NullPointException
        Member writer = feed.getWriter();
        if (writer == null) {
            writer = Member.builder().build();
        }

        return FeedMegaphoneDto.builder()
                .feedId(feed.getFeedId())
                .writerId(writer.getMemberId())
                .writerNickname(writer.getNickname())
                .feedText(feed.getFeedText())
                .writtenTime(feed.getWrittenTime())
                .build();
    }

    public static List<FeedMegaphoneDto> toDtoList(List<Feed> feedList) {
        return feedList.stream().map(FeedMegaphoneDto::toDto).collect(Collectors.toList());
    }
}
