package com.kube.noon.feed.repository;

import com.kube.noon.building.domain.Building;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import com.kube.noon.common.zzim.ZzimRepository;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
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

/**
 * 1. 피드 전체 가져오기
 * 2. 피드 일부 가져오기 : Pageable
 * 3. 피드 작성 수정, 삭제
 * 4. 피드 상세 보기
 * 5. 각종 피드 목록 가져오기
 */
@Log4j2
@SpringBootTest
@ActiveProfiles("winterhana")
public class TestFeedRepository {

    @Autowired
    private FeedRepository feedRepository;

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
        Member writer = new Member();
        writer.setMemberId(memberId);

        // 1. 회원별 피드 목록 가져오기
        List<Feed> getFeedListByWriter = feedRepository.findByWriterAndActivatedTrue(writer);
        assertThat(getFeedListByWriter.size()).isGreaterThan(0);
        for(Feed f : getFeedListByWriter) {
            log.info(f.toString());
        }

        // 2. 건물별 피드 목록 보기
        List<Feed> getFeedListByBuildingId = feedRepository.findByBuildingAndActivatedTrue(Building.builder().buildingId(buildingId).build());
        assertThat(getFeedListByBuildingId.size()).isGreaterThan(0);
        for(Feed f : getFeedListByBuildingId) {
            log.info(f.toString());
        }

        // 3. 회원이 좋아요를 한 피드 목록 보기
        List<Feed> getFeedListByMemberLikeFeed = feedRepository.findByMemberLikeFeed(writer);
        assertThat(getFeedListByMemberLikeFeed.size()).isGreaterThan(0);
        for(Feed f : getFeedListByMemberLikeFeed) {
            log.info(f.toString());
        }

        // 4. 회원이 북마크를 한 피드 목록 보기
        List<Feed> getFeedListByMemberBookmarkFeed = feedRepository.findByMemberBookmarkFeed(writer);
        assertThat(getFeedListByMemberBookmarkFeed.size()).isGreaterThan(0);
        for(Feed f : getFeedListByMemberBookmarkFeed) {
            log.info(f.toString());
        }

        // 5. 회원이 건물 구독을 한 피드 목록 보기
        List<Feed> getFeedListByBuildingSubscription = feedRepository.findByMemberBuildingSubscription(writer);
        assertThat(getFeedListByBuildingSubscription.size()).isGreaterThan(0);
        for(Feed f : getFeedListByBuildingSubscription) {
            log.info(f.toString());
        }
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

        Feed mainFeed = feedRepository.findByWriterAndMainActivatedTrue(member); // 1.
        mainFeed.setMainActivated(false); // 2.
        feedRepository.save(mainFeed);

        Feed setMainFeed = feedRepository.findByFeedId(10006); // 3.
        setMainFeed.setMainActivated(true); // 4.
        feedRepository.save(setMainFeed);

        assertThat(feedRepository.findByFeedId(10006).isMainActivated()).isEqualTo(true);
    }

    /**
     * 피드의 좋아요를 추가하거나 삭제한다.
     * feed_id = 10001에서 member_id = 'member_1'이 좋아요를 추가 및 삭제한다.
     */
    @Transactional
    @Test
    public void addAndDeleteFeedLikeTest() {
        Member member = new Member();
        member.setMemberId("member_1");
        int feedId = 10001;
        
        // 미완
    }
}
