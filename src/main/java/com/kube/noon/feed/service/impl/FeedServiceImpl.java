package com.kube.noon.feed.service.impl;

import com.kube.noon.building.domain.Building;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.dto.FeedSummaryDto;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;

    @Override
    public List<FeedSummaryDto> getFeedListByMember(String memberId) {
        List<Feed> entities = feedRepository.findByWriterAndActivatedTrue(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMember = FeedSummaryDto.toDtoList(entities);

        return feedListByMember;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuilding(int buildingId) {
        List<Feed> entities = feedRepository.findByBuildingAndActivatedTrue(
                Building.builder()
                        .buildingId(buildingId)
                        .build()
        );

        List<FeedSummaryDto> feedListByBuilding = FeedSummaryDto.toDtoList(entities);

        return feedListByBuilding;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberLike(String memberId) {
        List<Feed> entites = feedRepository.findByMemberLikeFeed(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMemberLike = FeedSummaryDto.toDtoList(entites);

        return feedListByMemberLike;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberBookmark(String memberId) {
        List<Feed> entites = feedRepository.findByMemberBookmarkFeed(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMemberBookmark = FeedSummaryDto.toDtoList(entites);

        return feedListByMemberBookmark;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuildingSubscription(String memberId) {
        List<Feed> entites = feedRepository.findByMemberBuildingSubscription(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByBuildingSubscription = FeedSummaryDto.toDtoList(entites);

        return feedListByBuildingSubscription;
    }

    @Transactional
    @Override
    public int addFeed(FeedDto feedDto) {
        Feed addFeed = FeedDto.toEntity(feedDto);
        return feedRepository.save(addFeed).getFeedId();
    }

    @Transactional
    @Override
    public int updateFeed(FeedDto feedDto) {
        Feed updateFeed = feedRepository.findByFeedId(feedDto.getFeedId());

        updateFeed.setTitle(feedDto.getTitle());
        updateFeed.setFeedText(feedDto.getFeedText());
        updateFeed.setModified(true);
        updateFeed.setFeedCategory(feedDto.getFeedCategory());
        return feedRepository.save(updateFeed).getFeedId();
    }

    @Transactional
    @Override
    public int deleteFeed(FeedDto feedDto) {
        Feed deleteFeed = feedRepository.findByFeedId(feedDto.getFeedId());

        deleteFeed.setActivated(false);
        return feedRepository.save(deleteFeed).getFeedId();
    }

    @Override
    public FeedDto getFeedById(int feedId) {
        Feed getFeed = feedRepository.findByFeedId(feedId);
        return FeedDto.toDto(getFeed);
    }

    @Transactional
    @Override
    public int setPublicRage(FeedDto feedDto) {
        Feed setPublicRangeFeed = feedRepository.findByFeedId(feedDto.getFeedId());

        setPublicRangeFeed.setPublicRange(feedDto.getPublicRange());
        return feedRepository.save(setPublicRangeFeed).getFeedId();
    }

    @Transactional
    @Override
    public int setMainFeed(FeedDto feedDto) {

        // 1. 등록된 대표 피드 취소
        List<Feed> mainFeeds = feedRepository.findByWriterAndMainActivatedTrue(Member.builder().memberId(feedDto.getWriterId()).build());
        if(mainFeeds != null) {
            mainFeeds.stream().forEach(s -> {
                s.setMainActivated(false);
                feedRepository.save(s);
            });
        }

        // 2. 새로운 대표 피드 등록
        Feed setMainFeed = feedRepository.findByFeedId(feedDto.getFeedId());
        setMainFeed.setMainActivated(true);
        return feedRepository.save(setMainFeed).getFeedId();
    }

    @Override
    public List<FeedSummaryDto> searchFeedList(String keyword) {
        List<Feed> searchFeedTextList = feedRepository.findByFeedTextContainingIgnoreCase(keyword);
        List<Feed> searchFeedTitleList = feedRepository.findByTitleContainingIgnoreCase(keyword);

        searchFeedTextList.addAll(searchFeedTitleList);

        List<FeedSummaryDto> searchFeedList = FeedSummaryDto.toDtoList(searchFeedTextList);

        return searchFeedList;
    }

    @Override
    public int setViewCntUp(int feedId) {
        Feed feed = feedRepository.findByFeedId(feedId);
        feed.setViewCnt(feed.getViewCnt() + 1);

        return feedRepository.save(feed).getFeedId();
    }
}
