package com.kube.noon.feed.service;

import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.dto.FeedSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 피드 자체에 대한 기능을 service로 구현하였다.
 * 추가, 수정, 삭제에 대한 return 값은 대상 테이블의 PK 값이다.
 * 예시 ) 피드를 수정함 : 그 피드의 feed_id를 return 한다.
 */
@Service
public interface FeedService {
    // 회원별 피드 목록을 가져온다,
    List<FeedSummaryDto> getFeedListByMember(String memberId);

    // 건물별 피드 목록을 가져온다. 필요에 따라 피드를 추천한다.
    List<FeedSummaryDto> getFeedListByBuilding(String memberId, int buildingId);

    // 건물별 피드 목록을 가져온다.
    List<FeedSummaryDto> getFeedListByBuilding(int buildingId);

    // 회원이 좋아요를 누른 피드 목록을 가져온다.
    List<FeedSummaryDto> getFeedListByMemberLike(String memberId);

    // 회원이 북마크를 누른 피드 목록을 가져온다.
    List<FeedSummaryDto> getFeedListByMemberBookmark(String memberId);

    // 회원이 건물을 구독한 피드 목록을 가져온다.
    List<FeedSummaryDto> getFeedListByBuildingSubscription(String memberId);

    // 피드를 추가한다.
    int addFeed(FeedDto feedDto);

    // 피드를 수정한다.
    int updateFeed(FeedDto feedDto);

    // 피드를 삭제한다.
    int deleteFeed(int feedId);

    // 피드 하나를 상세보기한다.
    FeedDto getFeedById(int feedId);

    // 피드의 공개 범위를 설정한다.
    int setPublicRage(FeedDto feedDto);

    // 맴버의 메인 피드를 설정한다.
    int setMainFeed(FeedDto feedDto);

    // 피드의 제목이나 택스트로 검색한다.
    List<FeedSummaryDto> searchFeedList(String keyword);

    // 피드의 조회수를 1 올린다.
    int setViewCntUp(int feedId);
}
