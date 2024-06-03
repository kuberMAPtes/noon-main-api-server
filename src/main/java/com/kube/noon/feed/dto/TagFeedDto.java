package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import lombok.*;

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
}
