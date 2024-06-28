package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedEvent;
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
public class FeedEventDto {
    private int feedId;
    private LocalDateTime eventDate;

    public static FeedEventDto toDto(FeedEvent feedEvent) {
        return FeedEventDto.builder()
                .feedId(feedEvent.getFeedId())
                .eventDate(feedEvent.getEventDate())
                .build();
    }

    public static List<FeedEventDto> toDtoList(List<FeedEvent> feedEventList) {
        return feedEventList.stream().map(FeedEventDto::toDto).collect(Collectors.toList());
    }
}
