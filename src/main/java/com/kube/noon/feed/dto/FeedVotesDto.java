package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedVotes;
import lombok.*;

import java.util.List;

/**
 * 투표 정보를 가져오기 위한 Dto
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedVotesDto {
    private Feed feed;
    private int feedId;
    private String question;
    private List<String> options;
    private List<Integer> votes;

    public static FeedVotesDto toDto(FeedVotes feedVotes) {
        return FeedVotesDto.builder()
                .feed(feedVotes.getFeed())
                .feedId(feedVotes.getFeedId())
                .question(feedVotes.getQuestion())
                .options(feedVotes.getOptions())
                .votes(feedVotes.getVotes())
                .build();
    }

}
