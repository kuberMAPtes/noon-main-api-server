package com.kube.noon.feed.service;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.TagFeed;
import com.kube.noon.feed.dto.FeedDto;
import com.kube.noon.feed.dto.FeedSummaryDto;
import com.kube.noon.feed.dto.UpdateFeedDto;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.feed.service.impl.FeedServiceImpl;
import com.kube.noon.feed.service.impl.FeedStatisticsServiceImpl;
import com.kube.noon.feed.service.recommend.FeedRecommendationMemberId;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
@ActiveProfiles("winterhana")
public class TestFeedServiceImpl {

    @Autowired
    private FeedServiceImpl feedServiceImpl;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private FeedStatisticsServiceImpl feedStatisticsServiceImpl;
    
    @Autowired
    private ZzimRepository zzimRepository;

    /**
     * 피드 목록을 가져오는 테스트를 한다.
     * memberId = "member_1", buildingId = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void getListTest() {
        List<FeedSummaryDto> feedListByMember = feedServiceImpl.getFeedListByMember("member_1", 0, 10);
        assertThat(feedListByMember).isNotNull();
        assertThat(feedListByMember.size()).isGreaterThan(0);
        for (FeedSummaryDto feedSummaryDto : feedListByMember) {
            log.info(feedSummaryDto);
        }

        List<FeedSummaryDto> feedListByBuilding = feedServiceImpl.getFeedListByBuilding("member_3", 10001, 0, 10);
        assertThat(feedListByBuilding).isNotNull();
        assertThat(feedListByBuilding.size()).isGreaterThan(0);
        for (FeedSummaryDto feedSummaryDto : feedListByBuilding) {
            log.info(feedSummaryDto);
        }

        List<FeedSummaryDto> feedListByMemberLike = feedServiceImpl.getFeedListByMemberLike("member_1", 0, 10);
        assertThat(feedListByMemberLike).isNotNull();
        assertThat(feedListByMemberLike.size()).isGreaterThan(0);
        for (FeedSummaryDto feedSummaryDto : feedListByMemberLike) {
            log.info(feedSummaryDto);
        }

        List<FeedSummaryDto> feedListByMemberBookmark = feedServiceImpl.getFeedListByMemberBookmark("member_1", 0, 10);
        assertThat(feedListByMemberBookmark).isNotNull();
        assertThat(feedListByMemberBookmark.size()).isGreaterThan(0);
        for (FeedSummaryDto feedSummaryDto : feedListByMemberBookmark) {
            log.info(feedSummaryDto);
        }

        List<FeedSummaryDto> feedListByBuildingSubscription = feedServiceImpl.getFeedListByBuildingSubscription("member_1", 0, 10);
        assertThat(feedListByBuildingSubscription).isNotNull();
        assertThat(feedListByBuildingSubscription.size()).isGreaterThan(0);
        for (FeedSummaryDto feedSummaryDto : feedListByBuildingSubscription) {
            log.info(feedSummaryDto);
        }
    }

    /**
     * 피드를 추가한다.
     * 글을 하나도 적지 않은 건물 내에서 긍을 처음 작성하는 유저 대상으로 테스트한다.
     * building_id = 10015, writer_id = 'member_15'
     */
    @Transactional
    @Test
    public void addFeedTest() {
        // 태그 추가
        List<String> updateTagList = new ArrayList<>();
        updateTagList.add("집에");
        updateTagList.add("가고");
        updateTagList.add("싶다");

        FeedDto feedDto = FeedDto.builder()
                .writerId("member_15")
                .buildingId(10015)
                .mainActivate(false)
                .publicRange(PublicRange.PUBLIC)
                .title("WinterHana Test")
                .feedText("테스트 중입니다.")
                .viewCnt(9999L)
                .writtenTime(LocalDateTime.now())
                .feedCategory(FeedCategory.GENERAL)
                .modified(false)
                .updateTagList(updateTagList)
                .build();

        int feedId = feedServiceImpl.addFeed(feedDto);

        FeedDto getFeedDto = feedServiceImpl.getFeedById(feedId);

        log.info(getFeedDto);

        assertThat(getFeedDto).isNotNull();
        assertThat(getFeedDto.getWriterId()).isEqualTo("member_15");
        assertThat(getFeedDto.getBuildingId()).isEqualTo(10015);
        assertThat(getFeedDto.getTitle()).isEqualTo("WinterHana Test");
        assertThat(getFeedDto.getTagFeeds().size()).isEqualTo(3);
        assertThat(getFeedDto.getTags().size()).isEqualTo(3);
    }

    /**
     * feed_id = 10000인 피드를 수정한다. 이때, 피으의 Text를 수정하고 확인한다.
     */
    @Transactional
    @Test
    public void updateFeedTest() {
        List<String> updateTagList = new ArrayList<>();
        updateTagList.add("집에");
        updateTagList.add("가고");
        updateTagList.add("싶다");

        FeedDto feedDto = feedServiceImpl.getFeedById(10000);
        UpdateFeedDto updateFeedDto = UpdateFeedDto.builder()
                .feedId(feedDto.getFeedId())
                .feedText("수정 테스트 중입니다.")
                .title(feedDto.getTitle())
                .publicRange(feedDto.getPublicRange())
                .feedCategory(feedDto.getFeedCategory())
                .updateTagList(updateTagList)
                .build();

        int feedId = feedServiceImpl.updateFeed(updateFeedDto);

        FeedDto getFeedDto = feedServiceImpl.getFeedById(feedId);

        log.info(getFeedDto);

        assertThat(getFeedDto).isNotNull();
        assertThat(getFeedDto.getWriterId()).isEqualTo("member_1");
        assertThat(getFeedDto.getBuildingId()).isEqualTo(10001);
        assertThat(getFeedDto.getTitle()).isEqualTo("Title_1");
        assertThat(getFeedDto.getFeedText()).isEqualTo("수정 테스트 중입니다.");
        assertThat(getFeedDto.getTagFeeds().size()).isEqualTo(3);
        assertThat(getFeedDto.getTags().size()).isEqualTo(3);
    }

    /**
     * feed_id = 10000인 피드를 삭제, 즉 비공개한다.
     */
    @Transactional
    @Test
    public void deleteFeedTest() {
        int feedId = feedServiceImpl.deleteFeed(10000);

        // 지워진 피드는 Repository 내에서 확인 가능하다.
        Feed getFeed = feedRepository.findByFeedId(feedId);

        assertThat(getFeed).isNotNull();
        assertThat(getFeed.isActivated()).isFalse();
    }

    /**
     * 피드의 공개 범위를 수정한다.
     * feed_id = 10010을 기준으로 PRIVATE로 변경한다.
     */
    @Transactional
    @Test
    public void setPublicRangeTest() {
        FeedDto setPublicRangeFeedDto = FeedDto.builder()
                .feedId(10010)
                .publicRange(PublicRange.PRIVATE)
                .build();

        int feedId = feedServiceImpl.setPublicRage(setPublicRangeFeedDto);

        FeedDto getFeedDto = feedServiceImpl.getFeedById(feedId);

        assertThat(getFeedDto.getFeedId()).isEqualTo(10010);
        assertThat(getFeedDto.getPublicRange()).isEqualTo(PublicRange.PRIVATE);
    }

    /**
     * 피드의 메인 피드를 수정한다.
     * writer_id = "member_1"의 메인 피드를 feed_id = 10008로 수정한다.
     */
    @Transactional
    @Test
    public void setMainFeedTest() {
        FeedDto setMainFeedDto = FeedDto.builder()
                .feedId(10008)
                .writerId("member_1")
                .build();

        int feedId = feedServiceImpl.setMainFeed(setMainFeedDto);

        FeedDto getFeedDto = feedServiceImpl.getFeedById(feedId);

        assertThat(getFeedDto).isNotNull();
        assertThat(getFeedDto.isMainActivate()).isTrue();
    }

    /**
     * 피드의 제목이나 내용을 기준으로 검색한다.
     */
    @Transactional
    @Test
    public void searchFeedListTest() {
        String keyword = "feed 1";

        List<FeedSummaryDto> searchFeedList = feedServiceImpl.searchFeedList(keyword, 0, 10);

        assertThat(searchFeedList.size()).isGreaterThan(0);
        for(FeedSummaryDto f : searchFeedList) {
            log.info(f.toString());
        }
    }

    /**
     * 피드의 조회수를 1 증가시킨다.
     */
    @Transactional
    @Test
    public void setViewCntUpTest() {
        FeedDto beforeViewCntDto = feedServiceImpl.getFeedById(10000);
        int beforeViewCnt = beforeViewCntDto.getViewCnt().intValue();

        int feedId = feedServiceImpl.setViewCntUp(10000);

        FeedDto afterViewCntDto = feedServiceImpl.getFeedById(feedId);
        int afterViewCnt = afterViewCntDto.getViewCnt().intValue();

        assertThat(afterViewCnt).isEqualTo(beforeViewCnt + 1);
    }

    /**
     * 유저와 비슷한 성향을 가진 유저 이름 추천 -> 피드 추천 알고리즘 생성 시 사용함
     */
    @Transactional
    @Test
    public void getMeberLikeTagsRecommendationTest() {
        FeedRecommendationMemberId.initData(feedStatisticsServiceImpl.getMemberLikeTag());
        List<String> memberIdList = FeedRecommendationMemberId.getMemberLikeTagsRecommendation("member_1");

        if(memberIdList == null || memberIdList.isEmpty()) {
            assertThat(false).isTrue();
        } else {
            assertThat(memberIdList.size()).isGreaterThan(0);
            memberIdList.stream().forEach(System.out::println);
        }
    }

    /**
     * ZzimList를 가져오는 것에 대한 테스트, 값만 가져온다.
     */
    @Transactional
    @Test
    public void getZzimListTest() {
        List<Integer> zzimFeedIdList = zzimRepository.getFeedIdByMemberIdAndZzimType("member_1", ZzimType.LIKE);

        System.out.println(zzimFeedIdList.size());

        for(int feedId : zzimFeedIdList) {
            System.out.println("FeedId : " + feedId);
        }
    }
}
