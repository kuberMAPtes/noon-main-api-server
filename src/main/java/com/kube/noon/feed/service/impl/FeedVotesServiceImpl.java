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

import java.util.*;

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
                .voterIds(new HashMap<String, Integer>())
                .build();

        return FeedVotesDto.toDto(feedVotesRepository.save(feedVotes));
    }

    @Transactional
    @Override
    public FeedVotesDto updateVote(FeedVotesDto feedVotesDto) {
        FeedVotes feedVotes = feedVotesRepository.findById(feedVotesDto.getFeedId()).orElse(null);
        feedVotes.setQuestion(feedVotesDto.getQuestion());
        feedVotes.setOptions(feedVotesDto.getOptions());
        feedVotes.setVotes(new ArrayList<>(Collections.nCopies(feedVotesDto.getOptions().size(), 0))); // 초기화
        feedVotes.setVoterIds(new HashMap<String, Integer>()); // 초기화

        return FeedVotesDto.toDto(feedVotesRepository.save(feedVotes));
    }

    @Transactional
    @Override
    public void deleteVote(int feedId) {
        feedVotesRepository.deleteById(feedId);
    }

    @Transactional
    @Override
    public FeedVotesDto addVoting(FeedVotesDto feedVotesDto) {
        FeedVotes feedVotes = feedVotesRepository.findById(feedVotesDto.getFeedId()).orElse(null);
        // System.out.println(feedVotes);

        if(feedVotes != null) {
            // 1. 투표 수 더하기
            List<Integer> votes = feedVotes.getVotes();
            int chosenOption = feedVotesDto.getChosenOption();
            votes.set(chosenOption, votes.get(chosenOption) + 1);
            feedVotes.setVotes(votes);

            // 2. 투표를 한 맴버 추가
            Map<String, Integer> voterIds = feedVotes.getVoterIds();
            voterIds.put(feedVotesDto.getMemberId(), chosenOption);
            feedVotes.setVoterIds(voterIds);

            return FeedVotesDto.toDto(feedVotesRepository.save(feedVotes));
        }

        return null;
    }


    @Override
    public FeedVotesDto getVoteById(int feedId) {
        return FeedVotesDto.toDto(
                feedVotesRepository.findById(feedId)
                        .orElse(null)
        );
    }
}
