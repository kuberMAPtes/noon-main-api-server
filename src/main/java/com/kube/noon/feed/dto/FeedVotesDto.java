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
public class FeedVotesDto {
    private int feedId;
    private String question;
    private List<String> options;
    private List<Integer> votes;

    public static FeedVotesDto toDto(FeedVotes feedVotes) {
        if(feedVotes == null) return new FeedVotesDto(); // null 처리

        return FeedVotesDto.builder()
                .feedId(feedVotes.getFeedId())
                .question(feedVotes.getQuestion())
                .options(feedVotes.getOptions())
                .votes(feedVotes.getVotes())
                .build();
    }

    @Override
    public String toString() {
        return "feedId : " + feedId +
                ", question : " + question +
                ", options : " + options +
                ", votes : " + votes;
    }
}
