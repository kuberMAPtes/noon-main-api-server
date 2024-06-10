package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import com.kube.noon.feed.domain.TagFeed;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TagFeedDto {
    private int tagFeedId;
    private Feed feed;
    private Tag tag;

    public static TagFeedDto toDto(TagFeed tagFeed) {
        return TagFeedDto.builder()
                .tagFeedId(tagFeed.getTagFeedId())
                .feed(tagFeed.getFeed())
                .tag(tagFeed.getTag())
                .build();
    }

    public static TagFeed toEntity(TagFeedDto tagFeedDto) {
        return TagFeed.builder()
                .tagFeedId(tagFeedDto.getTagFeedId())
                .feed(tagFeedDto.getFeed())
                .tag(tagFeedDto.getTag())
                .build();
    }

    public static List<TagFeedDto> toDtoList(List<TagFeed> tagFeedDtoList) {
        return tagFeedDtoList.stream().map(TagFeedDto::toDto).collect(Collectors.toList());
    }

}
