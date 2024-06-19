package com.kube.noon.feed.service.impl;

import com.kube.noon.building.domain.Building;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import com.kube.noon.feed.domain.TagFeed;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.dto.FeedSummaryDto;
import com.kube.noon.feed.dto.TagDto;
import com.kube.noon.feed.dto.UpdateFeedDto;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.feed.repository.TagFeedRepository;
import com.kube.noon.feed.repository.TagRepository;
import com.kube.noon.feed.repository.mybatis.FeedMyBatisRepository;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.feed.service.recommend.FeedRecommendationMemberId;
import com.kube.noon.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final FeedMyBatisRepository feedMyBatisRepository;
    private final TagRepository tagRepository;
    private final TagFeedRepository tagFeedRepository;
    private final ZzimRepository zzimRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // FeedSumaryDto에 좋아요, 북마크 정보 저장
    private List<FeedSummaryDto> setFeedSummaryDtoLikeAndBookmark(String memberId, List<FeedSummaryDto> feedList) {
        List<Integer> zzimLikeList = zzimRepository.getFeedIdByMemberIdAndZzimType(memberId, ZzimType.LIKE);
        List<Integer> zzimBookmarkList = zzimRepository.getFeedIdByMemberIdAndZzimType(memberId, ZzimType.BOOKMARK);

        return feedList.stream()
                .map(feed -> {
                    if (zzimLikeList.contains(feed.getFeedId())) {
                        feed.setLike(true);
                    }
                    if (zzimBookmarkList.contains(feed.getFeedId())) {
                        feed.setBookmark(true);
                    }
                    return feed;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMember(String memberId) {
        List<Feed> entities = feedRepository.findByWriterAndActivatedTrue(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMember = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entities));

        return feedListByMember;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMember(String memberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entities = feedRepository.findByWriterAndActivatedTrue(
                Member.builder().memberId(memberId).build(),
                pageable
        );

        List<FeedSummaryDto> feedListByMember = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entities));

        return feedListByMember;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuilding(String memberId, int buildingId) {
        Building building = Building.builder().buildingId(buildingId).build();
        List<Feed> entities = new ArrayList<>();

        FeedRecommendationMemberId.initData(feedMyBatisRepository.getMemberLikeTag());
        List<String> memberIdList = FeedRecommendationMemberId.getMemberLikeTagsRecommendation(memberId);

        // 추천 맴버가 없다면 빌딩 그대로 보여주기
        if(memberIdList == null || memberIdList.isEmpty()) {
            entities = feedRepository.findByBuildingAndActivatedTrue(building);
        } else {
            Random rand = new Random();
            String recommandMemberId = memberIdList.get(rand.nextInt(memberIdList.size()));
            Member recommandMember = Member.builder().memberId(recommandMemberId).build();

            entities = feedRepository.findFeedWithLikesFirst(recommandMember, building);
        }

        List<FeedSummaryDto> feedListByBuilding = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entities));

        return feedListByBuilding;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuilding(String memberId, int buildingId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        Building building = Building.builder().buildingId(buildingId).build();
        List<Feed> entities = new ArrayList<>();

        FeedRecommendationMemberId.initData(feedMyBatisRepository.getMemberLikeTag());
        List<String> memberIdList = FeedRecommendationMemberId.getMemberLikeTagsRecommendation(memberId);

        // 추천 맴버가 없다면 빌딩 그대로 보여주기
        if(memberIdList == null || memberIdList.isEmpty()) {
            entities = feedRepository.findByBuildingAndActivatedTrue(building, pageable);
        } else {
            Random rand = new Random();
            String recommandMemberId = memberIdList.get(rand.nextInt(memberIdList.size()));
            Member recommandMember = Member.builder().memberId(recommandMemberId).build();

            entities = feedRepository.findFeedWithLikesFirst(recommandMember, building, pageable);
        }

        List<FeedSummaryDto> feedListByBuilding = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entities));

        return feedListByBuilding;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuilding(int buildingId) {
        Building building = Building.builder().buildingId(buildingId).build();
        List<Feed> entities = feedRepository.findByBuildingAndActivatedTrue(building);

        return FeedSummaryDto.toDtoList(entities);
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuilding(int buildingId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        Building building = Building.builder().buildingId(buildingId).build();
        List<Feed> entities = feedRepository.findByBuildingAndActivatedTrue(building, pageable);

        return FeedSummaryDto.toDtoList(entities);
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberLike(String memberId) {
        List<Feed> entites = feedRepository.findByMemberLikeFeed(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMemberLike = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberLike;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberLike(String memberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entites = feedRepository.findByMemberLikeFeed(
                Member.builder().memberId(memberId).build(), pageable
        );

        List<FeedSummaryDto> feedListByMemberLike = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberLike;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberBookmark(String memberId) {
        List<Feed> entites = feedRepository.findByMemberBookmarkFeed(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMemberBookmark = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberBookmark;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberBookmark(String memberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entites = feedRepository.findByMemberBookmarkFeed(
                Member.builder().memberId(memberId).build(), pageable
        );

        List<FeedSummaryDto> feedListByMemberBookmark = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberBookmark;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuildingSubscription(String memberId) {
        List<Feed> entites = feedRepository.findByMemberBuildingSubscription(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByBuildingSubscription = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByBuildingSubscription;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuildingSubscription(String memberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entites = feedRepository.findByMemberBuildingSubscription(
                Member.builder().memberId(memberId).build(), pageable
        );

        List<FeedSummaryDto> feedListByBuildingSubscription = setFeedSummaryDtoLikeAndBookmark(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByBuildingSubscription;
    }

    @Transactional
    @Override
    public int addFeed(FeedDto feedDto) {
        Feed addFeed = FeedDto.toEntity(feedDto);
        if(addFeed.getFeedCategory() == FeedCategory.NOTICE) {
            addFeed.setBuilding(null);
        }
        addFeed.setActivated(true);

        int feedId = feedRepository.save(addFeed).getFeedId();

        List<String> updateTagList = feedDto.getUpdateTagList();

        // 태그는 각각 리스트를 받아서 추가한다.
        if(updateTagList != null && updateTagList.size() > 0) {
            for(String tagText : updateTagList) {
                // 1. 삽입 전 tag 테이블에 존재하는지 탐색
                Tag tag = tagRepository.findByTagText(tagText);

                // 1-1. 만약 테이블에 없다면 삽입하기
                if(tag == null) {
                    tag = Tag.builder().tagText(tagText).build();
                    tag = tagRepository.save(tag);
                }

                // 2. tag_feed 테이블에 추가하기
                Feed feed = Feed.builder().feedId(feedId).build();
                TagFeed tagFeed = TagFeed.builder().feed(feed).tag(tag).build();

                tagFeedRepository.save(tagFeed);
            }
        }

        entityManager.flush();
        entityManager.clear();

        return feedId;
    }

    @Transactional
    @Override
    public int updateFeed(UpdateFeedDto updateFeedDto) {
        int feedId = updateFeedDto.getFeedId();
        Feed updateFeed = feedRepository.findByFeedId(updateFeedDto.getFeedId());

        if(updateFeed.getFeedCategory() == FeedCategory.NOTICE) {
            updateFeed.setBuilding(null);
        }
        // 수정 가능한 내용 : 제목, 내용, 공개범위, 카테고리
        updateFeed.setTitle(updateFeedDto.getTitle());
        updateFeed.setFeedText(updateFeedDto.getFeedText());
        updateFeed.setModified(true);
        updateFeed.setPublicRange(updateFeedDto.getPublicRange());
        updateFeed.setFeedCategory(updateFeedDto.getFeedCategory());

        List<String> updateTagList = updateFeedDto.getUpdateTagList();

        // feed 내용 업데이트
        int updateFeedId = feedRepository.save(updateFeed).getFeedId();

        // 피드의 태그 정리
        // 0. 피드에 속한 모든 태그 삭제 -> 중복 태그 대비
        tagFeedRepository.deleteByFeed(updateFeed);

        entityManager.flush();
        entityManager.clear();

        if(updateTagList != null && updateTagList.size() > 0) {
            for(String tagText : updateTagList) {
                // 1. 삽입 전 tag 테이블에 존재하는지 탐색
                Tag tag = tagRepository.findByTagText(tagText);

                // 1-1. 만약 테이블에 없다면 삽입하기
                if(tag == null) {
                    tag = Tag.builder().tagText(tagText).build();
                    tag = tagRepository.save(tag);
                }

                System.out.println(tag.getTagId());

                // 2. tag_feed 테이블에 추가하기
                Feed feed = Feed.builder().feedId(feedId).build();
                TagFeed tagFeed = TagFeed.builder().feed(feed).tag(tag).build();

                tagFeedRepository.save(tagFeed);
            }
        }

        return updateFeedId;
    }

    @Transactional
    @Override
    public int deleteFeed(int feedId) {
        Feed deleteFeed = feedRepository.findByFeedId(feedId);

        deleteFeed.setActivated(false);
        return feedRepository.save(deleteFeed).getFeedId();
    }

    @Override
    public FeedDto getFeedById(int feedId) {
        Feed getFeed = feedRepository.findByFeedId(feedId);
        FeedDto resultFeed = FeedDto.toDto(getFeed);

        // tag의 목록을 가져온다.
        List<Tag> tagList = tagRepository.getTagByFeedId(Feed.builder().feedId(feedId).build());
        resultFeed.setTags(TagDto.toDtoList(tagList));

        return resultFeed;
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

        // 만약 피드가 없거나 자신의 피드가 아니라면 -1 반환
        if(setMainFeed == null || !setMainFeed.getWriter().equals(feedDto.getWriterId())) {
            return -1;
        }

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
    public List<FeedSummaryDto> searchFeedList(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> searchFeedList = feedRepository.searchFeedByKeyword(keyword, pageable);

        List<FeedSummaryDto> resultSearchFeedList = FeedSummaryDto.toDtoList(searchFeedList);

        return resultSearchFeedList;
    }

    @Override
    public int setViewCntUp(int feedId) {
        Feed feed = feedRepository.findByFeedId(feedId);
        feed.setViewCnt(feed.getViewCnt() + 1);

        return feedRepository.save(feed).getFeedId();
    }
}
