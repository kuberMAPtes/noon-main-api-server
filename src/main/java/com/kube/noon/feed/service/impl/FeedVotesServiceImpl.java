package com.kube.noon.feed.service.impl;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedVotes;
import com.kube.noon.feed.dto.FeedVotesDto;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.feed.repository.FeedVotesRepository;
import com.kube.noon.feed.service.FeedVotesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FeedVotesServiceImpl implements FeedVotesService {

    private final FeedVotesRepository feedVotesRepository;
    private final FeedRepository feedRepository;

    @Transactional
    @Override
    public FeedVotesDto addVote(FeedVotesDto feedVotesDto) {
        Feed feed = feedRepository.findByFeedId(feedVotesDto.getFeedId());
        FeedVotes feedVotes = FeedVotes.builder()
                .feed(feed)
                .question(feedVotesDto.getQuestion())
                .options(feedVotesDto.getOptions())
                .votes(new ArrayList<>(Collections.nCopies(feedVotesDto.getOptions().size(), 0))) // 0으로 초기화
                .build();

        return FeedVotesDto.toDto(feedVotesRepository.save(feedVotes));
    }

    @Transactional
    @Override
    public FeedVotesDto updateVote(FeedVotesDto feedVotesDto) {
        FeedVotes feedVotes = feedVotesRepository.findById(feedVotesDto.getFeedId()).orElseThrow(() -> new IllegalArgumentException("유효한 ID가 아님"));
        feedVotes.setQuestion(feedVotesDto.getQuestion());
        feedVotes.setOptions(feedVotesDto.getOptions());
        feedVotes.setVotes(new ArrayList<>(Collections.nCopies(feedVotesDto.getOptions().size(), 0))); // ㅊ기화

        return FeedVotesDto.toDto(feedVotesRepository.save(feedVotes));
    }

    @Transactional
    @Override
    public void deleteVote(int feedId) {
        feedVotesRepository.deleteById(feedId);
    }

    @Transactional
    @Override
    public void addVoting(int feedId, int optionIndex) {
        FeedVotes feedVotes = feedVotesRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Invalid feed ID"));
        List<Integer> votes = feedVotes.getVotes();
        votes.set(optionIndex, votes.get(optionIndex) + 1);
        feedVotes.setVotes(votes);
        feedVotesRepository.save(feedVotes);
    }

    @Override
    public void deleteVoting(int feedId, int optionIndex) {
        FeedVotes feedVotes = feedVotesRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Invalid feed ID"));
        List<Integer> votes = feedVotes.getVotes();
        votes.set(optionIndex, votes.get(optionIndex) - 1);
        feedVotes.setVotes(votes);
        feedVotesRepository.save(feedVotes);
    }


    @Override
    public FeedVotesDto getVoteById(int feedId) {
        return FeedVotesDto.toDto(
                feedVotesRepository.findById(feedId)
                        .orElseThrow(() -> new IllegalArgumentException("유효한 ID가 아님"))
        );
    }
}
