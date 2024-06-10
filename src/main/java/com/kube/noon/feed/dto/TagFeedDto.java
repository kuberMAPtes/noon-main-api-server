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
    private int feedId;
    private int tagId;

    public static TagFeedDto toDto(TagFeed tagFeed) {
        return TagFeedDto.builder()
                .tagFeedId(tagFeed.getTagFeedId())
                .feedId(tagFeed.getFeed().getFeedId())
                .tagId(tagFeed.getTag().getTagId())
                .build();
    }

    public static TagFeed toEntity(TagFeedDto tagFeedDto) {
        return TagFeed.builder()
                .tagFeedId(tagFeedDto.getTagFeedId())
                .feed(Feed.builder().feedId(tagFeedDto.getFeedId()).build())
                .tag(Tag.builder().tagId(tagFeedDto.getTagId()).build())
                .build();
    }

    public static List<TagFeedDto> toDtoList(List<TagFeed> tagFeedDtoList) {
        return tagFeedDtoList.stream().map(TagFeedDto::toDto).collect(Collectors.toList());
    }

}
