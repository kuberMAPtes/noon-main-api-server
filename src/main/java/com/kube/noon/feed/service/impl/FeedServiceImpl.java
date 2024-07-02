package com.kube.noon.feed.service.impl;

import com.kube.noon.building.domain.Building;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.*;
import com.kube.noon.feed.dto.*;
import com.kube.noon.feed.repository.*;
import com.kube.noon.feed.repository.mybatis.FeedMyBatisRepository;
import com.kube.noon.feed.service.FeedService;
import com.kube.noon.feed.service.recommend.FeedRecommendationMemberId;
import com.kube.noon.feed.util.CalculatorUtil;
import com.kube.noon.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final FeedMyBatisRepository feedMyBatisRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final TagRepository tagRepository;
    private final TagFeedRepository tagFeedRepository;
    private final ZzimRepository zzimRepository;
    private final FeedEventRepository feedEventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // FeedSumaryDto에 좋아요, 북마크 여부와 개수, 댓글 개수 저장
    private List<FeedSummaryDto> setFeedSummaryDtoInfo(String memberId, List<FeedSummaryDto> feedList) {
        if(memberId == null) return feedList;

        List<Integer> zzimLikeList = zzimRepository.getFeedIdByMemberIdAndZzimType(memberId, ZzimType.LIKE);
        List<Integer> zzimBookmarkList = zzimRepository.getFeedIdByMemberIdAndZzimType(memberId, ZzimType.BOOKMARK);

        if(feedList == null) {
            return new ArrayList<>();
        }
        
        return feedList.stream()
                .map(feed -> {
                    // 좋아요, 북마크 정보 저장
                    if (zzimLikeList.contains(feed.getFeedId())) {
                        feed.setLike(true);
                    }
                    if (zzimBookmarkList.contains(feed.getFeedId())) {
                        feed.setBookmark(true);
                    }

                    // 좋아요, 댓글 개수 저장
                    feed.setLikeCount(zzimRepository.getCountByFeedIdZzimType(feed.getFeedId(), ZzimType.LIKE));
                    feed.setCommentCount(feedCommentRepository.getFeedCommentCount(feed.getFeedId()));
                    return feed;
                })
                .collect(Collectors.toList());
    }

    // FeedDto에 좋아요, 북마크 여부를 저장한다.
    private FeedDto setFeedDtoLikeAndBookmark(String memberId, FeedDto feedDto) {
        List<Integer> zzimLikeList = zzimRepository.getFeedIdByMemberIdAndZzimType(memberId, ZzimType.LIKE);
        List<Integer> zzimBookmarkList = zzimRepository.getFeedIdByMemberIdAndZzimType(memberId, ZzimType.BOOKMARK);

        if(zzimLikeList != null && zzimLikeList.contains(feedDto.getFeedId())) {
            feedDto.setLike(true);
        }

        if(zzimBookmarkList != null && zzimBookmarkList.contains(feedDto.getFeedId())) {
            feedDto.setBookmark(true);
        }

        return feedDto;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMember(String memberId) {
        List<Feed> entities = feedRepository.findByWriterAndActivatedTrue(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMember = setFeedSummaryDtoInfo(memberId, FeedSummaryDto.toDtoList(entities));

        return feedListByMember;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMember(String memberId, String loginMemberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entities = feedRepository.findByWriterAndActivatedTrue(
                Member.builder().memberId(memberId).build(),
                pageable
        );

        List<FeedSummaryDto> feedListByMember = setFeedSummaryDtoInfo(loginMemberId, FeedSummaryDto.toDtoList(entities));

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

        List<FeedSummaryDto> feedListByBuilding = setFeedSummaryDtoInfo(memberId, FeedSummaryDto.toDtoList(entities));

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

        List<FeedSummaryDto> feedListByBuilding = setFeedSummaryDtoInfo(memberId, FeedSummaryDto.toDtoList(entities));

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

        List<FeedSummaryDto> feedListByMemberLike = setFeedSummaryDtoInfo(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberLike;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberLike(String memberId, String loginMemberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entites = feedRepository.findByMemberLikeFeed(
                Member.builder().memberId(memberId).build(), pageable
        );

        List<FeedSummaryDto> feedListByMemberLike = setFeedSummaryDtoInfo(loginMemberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberLike;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberBookmark(String memberId) {
        List<Feed> entites = feedRepository.findByMemberBookmarkFeed(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByMemberBookmark = setFeedSummaryDtoInfo(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberBookmark;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByMemberBookmark(String memberId, String loginMemberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entites = feedRepository.findByMemberBookmarkFeed(
                Member.builder().memberId(memberId).build(), pageable
        );

        List<FeedSummaryDto> feedListByMemberBookmark = setFeedSummaryDtoInfo(loginMemberId, FeedSummaryDto.toDtoList(entites));

        return feedListByMemberBookmark;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuildingSubscription(String memberId) {
        List<Feed> entites = feedRepository.findByMemberBuildingSubscription(
                Member.builder()
                        .memberId(memberId)
                        .build()
        );

        List<FeedSummaryDto> feedListByBuildingSubscription = setFeedSummaryDtoInfo(memberId, FeedSummaryDto.toDtoList(entites));

        return feedListByBuildingSubscription;
    }

    @Override
    public List<FeedSummaryDto> getFeedListByBuildingSubscription(String memberId, String loginMemberId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Feed> entites = feedRepository.findByMemberBuildingSubscription(
                Member.builder().memberId(memberId).build(), pageable
        );

        List<FeedSummaryDto> feedListByBuildingSubscription = setFeedSummaryDtoInfo(loginMemberId, FeedSummaryDto.toDtoList(entites));

        return feedListByBuildingSubscription;
    }

    @Override
    public List<FeedMegaphoneDto> getFeedListByBuildingAndMegaphone(int buildingId) {
        Building building = Building.builder().buildingId(buildingId).build();
        List<Feed> feedList = feedRepository.findByBuildingAndFeedCategoryAndActivatedTrue(building, FeedCategory.MEGAPHONE);

        return FeedMegaphoneDto.toDtoList(feedList);
    }

    @Override
    public List<FeedSummaryDto> getAllFeedOrderByPopolarity(String memberId, int page, int pageSize) {
        int offset = page * pageSize;
        List<FeedPopularityDto> feedPopularityDtoList = feedMyBatisRepository.getAllFeedOrderByPopolarity(pageSize, offset);
        List<FeedSummaryDto> allFeedOrderByPopolarity = new ArrayList<>();

        for(FeedPopularityDto f : feedPopularityDtoList) {
            allFeedOrderByPopolarity.add(FeedSummaryDto.toDto(feedRepository.findByFeedId(f.getFeedId())));
        }

        if (memberId == null || memberId.isEmpty()) { // 만약 memberId 정보가 없다면 그냥 리스트 출력
            return allFeedOrderByPopolarity;
        } else { // 만약 memberId 정보가 있다면 좋아요, 북마크 정보를 반영하고 출력
            return setFeedSummaryDtoInfo(memberId, allFeedOrderByPopolarity);
        }
    }

    @Transactional
    @Override
    public int addFeed(FeedDto feedDto) {
        // 한국 시간으로 변경
        ZonedDateTime kstDateTime = feedDto.getWrittenTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        feedDto.setWrittenTime(kstDateTime.toLocalDateTime());

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

        // 피드 종류가 이벤트일 때, 이벤트 지정하기
        FeedCategory feedCategory = feedDto.getFeedCategory();
        LocalDateTime eventDate = feedDto.getEventDate();
        if(feedCategory == FeedCategory.EVENT && eventDate != null) {
            FeedEvent event = FeedEvent.builder()
                    .feedId(feedId)
                    .eventDate(eventDate)
                    .build();

            feedEventRepository.save(event);
        }

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

        // 피드 종류가 이벤트일 때, 이벤트 지정하기
        FeedCategory feedCategory = updateFeedDto.getFeedCategory();
        LocalDateTime eventDate = updateFeedDto.getEventDate();
        if(feedCategory == FeedCategory.EVENT && eventDate != null) {
            FeedEvent event = FeedEvent.builder()
                    .feedId(feedId)
                    .eventDate(eventDate)
                    .build();

            feedEventRepository.save(event);
        }

        return updateFeedId;
    }

    @Transactional
    @Override
    public int deleteFeed(int feedId) {
        Feed deleteFeed = feedRepository.findByFeedId(feedId);
        deleteFeed.setActivated(false);

        // 첨부 파일 삭제 : activated = false
        for(FeedAttachment feedAttachment : deleteFeed.getAttachments()) {
            feedAttachment.setActivated(false);
        }

        // 댓글 삭제 : activated = false
        for(FeedComment feedComment : deleteFeed.getComments()) {
            feedComment.setActivated(false);
        }


        return feedRepository.save(deleteFeed).getFeedId();
    }

    @Override
    public FeedDto getFeedById(int feedId) {
        Feed getFeed = feedRepository.findByFeedId(feedId);
        FeedDto resultFeed = FeedDto.toDto(getFeed);

        // 좋아요, 북마크 개수 가져오기
        resultFeed.setLikeCount(zzimRepository.getCountByFeedIdZzimType(feedId, ZzimType.LIKE));
        resultFeed.setBookmarkCount(zzimRepository.getCountByFeedIdZzimType(feedId, ZzimType.BOOKMARK));

        // tag의 목록을 가져온다.
        List<Tag> tagList = tagRepository.getTagByFeedId(Feed.builder().feedId(feedId).build());
        resultFeed.setTags(TagDto.toDtoList(tagList));
        resultFeed.setUpdateTagList(tagList.stream().map(s -> s.getTagText()).collect(Collectors.toList()));

        // 피드가 이벤트 피드일 때 이벤트 날짜를 가져온다.
        if(getFeed.getFeedCategory() == FeedCategory.EVENT) {
            FeedEvent feedEvent = feedEventRepository.findByFeedId(feedId);
            if(feedEvent != null) {
                resultFeed.setEventDate(feedEvent.getEventDate());
            }
        }

        return resultFeed;
    }

    @Override
    public FeedDto getFeedById(String memberId, int feedId) {
        Feed getFeed = feedRepository.findByFeedId(feedId);
        FeedDto resultFeed = FeedDto.toDto(getFeed);

        // 좋아요, 북마크 개수 가져오기
        resultFeed.setLikeCount(zzimRepository.getCountByFeedIdZzimType(feedId, ZzimType.LIKE));
        resultFeed.setBookmarkCount(zzimRepository.getCountByFeedIdZzimType(feedId, ZzimType.BOOKMARK));
        resultFeed.setPopularity(CalculatorUtil.calPopularity(resultFeed));

        // tag의 목록을 가져온다.
        List<Tag> tagList = tagRepository.getTagByFeedId(Feed.builder().feedId(feedId).build());
        resultFeed.setTags(TagDto.toDtoList(tagList));

        return setFeedDtoLikeAndBookmark(memberId, resultFeed);
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

    @Override
    public List<FeedEventDto> getFeedEventList(int buildingId) {
        List<FeedEventDto> feedEventDtoList = feedRepository.findFeedWithEventDates(buildingId);

        return feedEventDtoList;
    }
}
