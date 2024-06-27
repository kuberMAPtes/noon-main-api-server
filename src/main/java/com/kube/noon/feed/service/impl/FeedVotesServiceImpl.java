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

        if (feedVotes != null) {
            // 1. 투표자의 현황 갱신 및 추가하기
            Map<String, Integer> voterIds = feedVotes.getVoterIds();
            String memberId = feedVotesDto.getMemberId();
            int chosenOption = feedVotesDto.getChosenOption();
            int optionSize = feedVotesDto.getOptions().size();
            voterIds.put(memberId, chosenOption);

            // 2. 투표 수 갱신하기
            List<Integer> votes = updateVotes(voterIds, optionSize);

            // 3. repository에 내용 갱신
            feedVotes.setVoterIds(voterIds);
            feedVotes.setVotes(votes);

            return FeedVotesDto.toDto(feedVotesRepository.save(feedVotes));
        } else {
            return null;
        }
    }




    @Override
    public FeedVotesDto getVoteById(int feedId) {
        return FeedVotesDto.toDto(
                feedVotesRepository.findById(feedId)
                        .orElse(null)
        );
    }

    // feedVotes 내의 voterIds를 확인하여 votes를 갱신한다.
    private List<Integer> updateVotes(Map<String, Integer> voterIds, int optionSize) {
        // 1. 새로운 배열을 만듬
        List<Integer> votes = new ArrayList<>(Collections.nCopies(optionSize, 0));

        // 2. 투표 개수를 계산할 Map을 만듬
        Map<Integer, Integer> valueCountMap = new HashMap<>();
        for (Integer value : voterIds.values()) {
            valueCountMap.put(value, valueCountMap.getOrDefault(value, 0) + 1);
        }
        
        // 3. 각 투표 계산 결과를 votes에 반영
        Iterator<Integer> iterator = valueCountMap.keySet().iterator();
        while (iterator.hasNext()) {
            int key = iterator.next();
            votes.set(key, valueCountMap.get(key));
        }
        
        return votes;
    }
}
