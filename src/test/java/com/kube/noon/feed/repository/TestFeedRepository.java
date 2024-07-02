package com.kube.noon.feed.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.member.domain.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
@ActiveProfiles("winterhana")
public class TestFeedRepository {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private ZzimRepository zzimRepository;

    /**
     * 피드의 전체 목록을 가져온다.
     * 단, Activated = true인 것들만 가져온다.
     */
    @Transactional
    @Test
    public void getFeedListTest() {
        List<Feed> feedList = feedRepository.findByActivatedTrue();

        assertThat(feedList).isNotNull();
        assertThat(feedList.size()).isGreaterThan(0);

        for(Feed f : feedList) {
            log.info(f.toString());
        }
    }

    /**
     * Spring Data JPA Pageable 객체를 통해서 피드 목록을 가져온다.
     * 100개의 데이터 중 처음 페이지 10개를 가져온다고 가정한다.
     */
    @Transactional
    @Test
    public void getFeedListByPagingTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Feed> feedPage = feedRepository.findAll(pageable);

        // test 1) page 객체 테스트
        assertThat(feedPage.getContent()).isNotNull();

        log.info("feedPage.getTotalPages: " + feedPage.getTotalPages());
        log.info("feedPage.getTotalElements: " + feedPage.getTotalElements());
        log.info("feedPage.getSize: " + feedPage.getSize());
    }

    /**
     * 피드의 추가, 수정, 삭제를 한 번에 테스트한다.
     * writer_id = "member_1", buildingId = 10000으로 가정한다.
     */
    @Transactional
    @Test
    public void addAndDeleteFeedTest() {
        // 임시로 만들고 대입
        Member member = new Member();
        member.setMemberId("member_1");

        // 1. 추가
        Feed initFeed = Feed.builder()
                .writer(member)
                .building(Building.builder().buildingId(10000).build())
                .mainActivated(false)
                .publicRange(PublicRange.PUBLIC)
                .title("WinterHana Test")
                .feedText("테스트 중입니다.")
                .viewCnt(9999L)
                .writtenTime(LocalDateTime.now())
                .feedCategory(FeedCategory.GENERAL)
                .modified(false)
                .activated(true)
                .build();

        int feedId = feedRepository.save(initFeed).getFeedId();
        Feed getInitFeed = feedRepository.findByFeedId(feedId);

        // test 1) 존재 여부 확인
        assertThat(getInitFeed).isNotNull();
        log.info(getInitFeed.toString());

        // 2. 수정
        Feed updateFeed = Feed.builder()
                .feedId(feedId)
                .writer(member)
                .building(Building.builder().buildingId(10000).build())
                .mainActivated(false)
                .publicRange(PublicRange.PUBLIC)
                .title("WinterHana Test")
                .feedText("수정된 피드 테스트 중입니다.")
                .viewCnt(9999L)
                .writtenTime(LocalDateTime.now())
                .feedCategory(FeedCategory.GENERAL)
                .modified(true)
                .activated(true)
                .build();

        feedRepository.save(updateFeed);
        Feed getUpdateFeed = feedRepository.findByFeedId(feedId);

        // test 2) 존재 여부 확인
        assertThat(getUpdateFeed).isNotNull();
        log.info(getUpdateFeed.toString());

        // 3. 삭제 - activated == false;
        Feed deleteFeed = feedRepository.findByFeedId(feedId);
        deleteFeed.setActivated(false);
        feedRepository.save(deleteFeed);

        Feed getDeleteFeed = feedRepository.findByFeedId(feedId);

        // test 3) 삭제 여부 확인
        assertThat(getDeleteFeed.isActivated()).isFalse();
        log.info(getDeleteFeed.toString());
    }

    /**
     * 피드 하나의 정보를 가져온다.
     * feed_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void getFeedDetailTest() {
        Feed feed = feedRepository.findByFeedId(10000);

        assertThat(feed.getFeedId()).isEqualTo(10000);
        log.info(feed.toString());
    }

    /**
     * 각 상황에 맞는 피드 리스트를 가져온다.
     * 기준 : member_id = "member_1", building_id = 10001
     */
    @Transactional
    @Test
    public void getFeedListByOrderTest() {
        String memberId = "member_1";
        int buildingId = 10001;
        Member writer = Member.builder().memberId(memberId).build();
        Building building = Building.builder().buildingId(buildingId).build();

        // 1. 회원별 피드 목록 가져오기
        List<Feed> getFeedListByWriter = feedRepository.findByWriterAndActivatedTrueOrderByWrittenTimeDesc(writer);
        assertThat(getFeedListByWriter.size()).isGreaterThan(0);
        for(Feed f : getFeedListByWriter) {
            log.info(f.toString());
        }

        // 2. 건물별 피드 목록 보기
//        List<Feed> getFeedListByBuildingId = feedRepository.findByBuildingAndActivatedTrue(building);
//        assertThat(getFeedListByBuildingId.size()).isGreaterThan(0);
//        for(Feed f : getFeedListByBuildingId) {
//            log.info(f.toString());
//        }
//
//        // 3. 회원이 좋아요를 한 피드 목록 보기
//        List<Feed> getFeedListByMemberLikeFeed = feedRepository.findByMemberLikeFeed(writer);
//        assertThat(getFeedListByMemberLikeFeed.size()).isGreaterThan(0);
//        for(Feed f : getFeedListByMemberLikeFeed) {
//            log.info(f.toString());
//        }
//
//        // 4. 회원이 북마크를 한 피드 목록 보기
//        List<Feed> getFeedListByMemberBookmarkFeed = feedRepository.findByMemberBookmarkFeed(writer);
//        assertThat(getFeedListByMemberBookmarkFeed.size()).isGreaterThan(0);
//        for(Feed f : getFeedListByMemberBookmarkFeed) {
//            log.info(f.toString());
//        }
//
//        // 5. 회원이 건물 구독을 한 피드 목록 보기
//        List<Feed> getFeedListByBuildingSubscription = feedRepository.findByMemberBuildingSubscription(writer);
//        assertThat(getFeedListByBuildingSubscription.size()).isGreaterThan(0);
//        for(Feed f : getFeedListByBuildingSubscription) {
//            log.info(f.toString());
//        }
//
//        // 6. 회원이 한 건물에서 좋아요를 누른 피드 목록을 가져온다.
//        List<Feed> getFeedListByMemberAndBuilding = feedRepository.findByMemberAndBuildingIdLikeFeed(writer, building);
//        assertThat(getFeedListByMemberAndBuilding.size()).isGreaterThan(0);
//        for(Feed f : getFeedListByMemberAndBuilding) {
//            log.info(f.toString());
//        }
//
//        // 7. 한 건물의 피드 중 특정 회원이 좋아요를 누른 피드 목록을 우선 정렬한다.
//        List<Feed> getFeedWithLikesFirst = feedRepository.findFeedWithLikesFirst(writer, building);
//        assertThat(getFeedListByMemberAndBuilding.size()).isGreaterThan(0);
//        for(Feed f : getFeedWithLikesFirst) {
//            log.info(f.toString());
//        }
    }

    /**
     * 피드 각각의 공개 범위를 수정한다.
     * feed_id = 10000을 기준으로 한다. PRIVATE로 바꾼다고 가정한다.
     */
    @Transactional
    @Test
    public void setPublicRangeTest() {
        Feed initFeed = feedRepository.findByFeedId(10000);
        log.info(initFeed.getPublicRange());

        Feed updateFeed = Feed.builder()
                .feedId(initFeed.getFeedId())
                .writer(initFeed.getWriter())
                .building(initFeed.getBuilding())
                .mainActivated(initFeed.isMainActivated())
                .publicRange(PublicRange.PRIVATE)    // 변경
                .title(initFeed.getTitle())
                .feedText(initFeed.getFeedText())
                .viewCnt(initFeed.getViewCnt())
                .writtenTime(LocalDateTime.now())    // 수정 시 수정 시간도 변경
                .feedCategory(initFeed.getFeedCategory())
                .modified(true)    // 변경되었으므로 true
                .activated(true)
                .build();

        feedRepository.save(updateFeed);
        assertThat(updateFeed.getPublicRange()).isEqualTo(PublicRange.PRIVATE);
        // log.info(initFeed.toString() + " / publicRage : " + initFeed.getPublicRange()); // 변화가 있는 모든 Entity 객체 수정 : 이것도 PRIVATE로 뜸
        log.info(updateFeed.toString() + " / publicRage : " + updateFeed.getPublicRange());
    }

    /**
     * 메인 피드를 설정한다.
     * 전체 시나리오 :
     * 1. writer_id = 'member_1'의 메인 피드을 가져온다. -> 2. 메인 피드를 취소한다. -> 3. 피드 번호에 맞는 피드를 가져온다. -> 4. 그 피드를 메인 피드로 한다.
     * 여기서는 feed_id = 10006을 메인 피드로 바꾼다.
     */
    @Transactional
    @Test
    public void setMainFeedTest() {
        Member member = new Member();
        member.setMemberId("member_1");

        // 메인 피드가 다중으로 설정되어 있을 수 있기 때문에 List로 받음
        List<Feed> mainFeeds = feedRepository.findByWriterAndMainActivatedTrue(member); // 1.

        assertThat(mainFeeds.size()).isGreaterThan(0);

        mainFeeds.stream().forEach(s -> {
            s.setMainActivated(false);
            feedRepository.save(s);
        }); // 2

        Feed setMainFeed = feedRepository.findByFeedId(10006); // 3.
        setMainFeed.setMainActivated(true); // 4.
        feedRepository.save(setMainFeed);


        assertThat(feedRepository.findByFeedId(10006).isMainActivated()).isEqualTo(true);
    }

    /**
     * 피드의 좋아요를 추가하거나 삭제한다.
     * building_id = 10002, feed_id = 10001에서 member_id = 'member_1'이 좋아요를 추가 및 삭제한다.
     */
    @Transactional
    @Test
    public void addAndDeleteFeedLikeTest() {
        String memberId = "member_1";
        int feedId = 10001;
        int buildingId = 10002;

        // 1. 추가
        // 1) 좋아요 데이터가 있는지 확인한다.
        List<Zzim> zzimLikeList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.LIKE);
        Zzim zzimLike = (zzimLikeList.isEmpty() ? null : zzimLikeList.get(0));
        Zzim resultZzim;

        if(zzimLike == null) { // 2) 없다면, 하나 추가한다.
            Zzim newZzimLike = Zzim.builder()
                    .memberId(memberId)
                    .feedId(feedId)
                    .zzimType(ZzimType.LIKE)
                    .buildingId(10001)
                    .subscriptionProviderId(null)
                    .activated(true)
                    .build();
            resultZzim = zzimRepository.save(newZzimLike);
        } else { // 3) 있다면. activated = true로 설정한다.
            zzimLike.setActivated(true);
            resultZzim = zzimRepository.save(zzimLike);
        }

        assertThat(resultZzim.isActivated()).isEqualTo(true);
        assertThat(resultZzim.getZzimType()).isEqualTo(ZzimType.LIKE);
        log.info(resultZzim.toString());

        // 2. 삭제
        List<Zzim> zzimDeleteLikeList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.LIKE);
        Zzim deleteZzim = (zzimDeleteLikeList.isEmpty() ? null : zzimDeleteLikeList.get(0));
        deleteZzim.setActivated(false);
        resultZzim = zzimRepository.save(deleteZzim);

        assertThat(resultZzim.isActivated()).isEqualTo(false);
        log.info(resultZzim.toString());
    }

    /**
     * 피드의 북마크를 추가하거나 삭제한다.
     * building_id = 10002, feed_id = 10001에서 member_id = 'member_1'이 북마크를 추가 및 삭제한다.
     */
    @Transactional
    @Test
    public void addAndDeleteFeedBookmarkTest() {
        String memberId = "member_1";
        int feedId = 10001;
        int buildingId = 10002;

        // 1. 추가
        // 1) 북마크 데이터가 있는지 확인한다.
        List<Zzim> zzimBookmarkList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.BOOKMARK);
        Zzim zzimBookmark = (zzimBookmarkList.isEmpty() ? null : zzimBookmarkList.get(0));
        Zzim resultZzim;

        if(zzimBookmark == null) { // 2) 없다면, 하나 추가한다.
            Zzim newZzimBookmark = Zzim.builder()
                    .memberId(memberId)
                    .feedId(feedId)
                    .zzimType(ZzimType.BOOKMARK)
                    .buildingId(10001)
                    .subscriptionProviderId(null)
                    .activated(true)
                    .build();
            resultZzim = zzimRepository.save(newZzimBookmark);
        } else { // 3) 있다면. activated = true로 설정한다.
            zzimBookmark.setActivated(true);
            resultZzim = zzimRepository.save(zzimBookmark);
        }

        assertThat(resultZzim.isActivated()).isEqualTo(true);
        assertThat(resultZzim.getZzimType()).isEqualTo(ZzimType.BOOKMARK);
        log.info(resultZzim.toString());

        // 2. 삭제
        List<Zzim> zzimDeleteBookmarkList = zzimRepository.findByFeedIdAndMemberIdAndZzimTypeOrderByZzimId(feedId, memberId, ZzimType.BOOKMARK);
        Zzim deleteZzim = (zzimDeleteBookmarkList.isEmpty() ? null : zzimDeleteBookmarkList.get(0));
        deleteZzim.setActivated(false);
        resultZzim = zzimRepository.save(deleteZzim);

        assertThat(resultZzim.isActivated()).isEqualTo(false);
        log.info(resultZzim.toString());
    }

    /**
     * 피드 내의 좋아요를 누른 회원의 목록을 가져온다.
     * feed_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void getFeedListByLinkInFeedTest() {
        List<Member> feedLikeMemberList = feedRepository.getFeedLikeList(Feed.builder().feedId(10000).build());

        assertThat(feedLikeMemberList.size()).isGreaterThan(0);
        for(Member member : feedLikeMemberList) {
            log.info(member.toString());
        }
    }

    /**
     * 피드를 제목이나 내용으로 검색한다.
     * keyword = Title_1로 한다.
     */
    @Transactional
    @Test
    public void searchFeedListTest() {
        String keyword = "Title_1";

        List<Feed> searchFeedTextList = feedRepository.findByFeedTextContainingIgnoreCase(keyword);
        List<Feed> searchFeedTitleList = feedRepository.findByTitleContainingIgnoreCase(keyword);

        searchFeedTextList.addAll(searchFeedTitleList);

        assertThat(searchFeedTextList.size()).isGreaterThan(0);
        for(Feed feed : searchFeedTextList) {
            log.info(feed.toString());
        }
    }
}
