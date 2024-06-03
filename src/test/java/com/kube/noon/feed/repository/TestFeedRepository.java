package com.kube.noon.feed.repository;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import com.kube.noon.feed.domain.Feed;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
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
        List<Feed> feedList = feedRepository.findAllByActivated(true);

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
        // 1. 추가
        Feed initFeed = Feed.builder()
                .writerId("member_1")
                .buildingId(10000)
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
                .writerId("member_1")
                .buildingId(10000)
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
}
