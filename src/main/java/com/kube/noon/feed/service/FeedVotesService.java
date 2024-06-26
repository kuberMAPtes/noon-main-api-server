package com.kube.noon.feed.service;

import com.kube.noon.feed.domain.FeedVotes;
import com.kube.noon.feed.dto.FeedVotesDto;

import java.util.List;

public interface FeedVotesService {
    // 투표를 생성한다.
    FeedVotesDto addVote(FeedVotesDto feedVotesDto);

    // 투표를 수정한다.
    FeedVotesDto updateVote(FeedVotesDto feedVotesDto);

    // 투표를 삭제한다.
    void deleteVote(int feedId);

    // 투표에 참여한다.
    void addVoting(int feedId, int optionIndex);

    // 투표 참여를 취소한다.
    void deleteVoting(int feedId, int optionIndex);

    // 특정 투표를 조회한다.
    FeedVotesDto getVoteById(int feedId);
}
