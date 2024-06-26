package com.kube.noon.customersupport.repository;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import com.kube.noon.customersupport.dto.notice.NoticeDto;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@SpringBootTest

public class TestCustomerSupportRepository {


    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private FeedRepository feedRepository;


    /**
     * 공지사항 목록을 가져온다. 삭제(activated=false)되지 않은 것만 가져온다.
     *
     */
    @Test
    void testfindNoticeList(){

        List<Feed> noticeList = noticeRepository.findByFeedCategoryAndActivated(FeedCategory.NOTICE, true);

        for(Feed notice : noticeList){
            log.info("공지사항={}",NoticeDto.fromEntity(notice));
        }

    }

    /**
     * 공지사항 목록을 가져온다. 삭제(activated=false)되지 않은 것만 가져온다.
     * Pageable을 통해 특정 페이지, 개수만 가져온다.
     *
     */
    @Test
    void testfindNoticeListByPageable(){

        Pageable pageable = PageRequest.of(0, 5);
        //Pageable pageable = PageRequest.of(1, 5);

        Page<Feed> noticePage = noticeRepository.findByFeedCategoryAndActivated(FeedCategory.NOTICE, true, pageable);

        log.info("feedPage.getContent: " + noticePage.getContent());
        log.info("feedPage.getTotalPages: " + noticePage.getTotalPages());
        log.info("feedPage.getTotalElements: " + noticePage.getTotalElements());
        log.info("feedPage.getSize: " + noticePage.getSize());

    }


    /**
     * feedRepository를 이용해 Notice를 Add 또는 Delete
     *
     */
    @Transactional
    @Test
    void testAddAndDeleteNotice() {

        Member member = new Member();
        member.setMemberId("member_1");

        //Notice Add Test
        Feed feed = Feed.builder()
                .writer(member)
                .building(null)
                .mainActivated(false)
                .publicRange(PublicRange.PUBLIC)
                .title("Test Notice")
                .feedText("This is Notice...")
                .viewCnt(9999L)
                .writtenTime(LocalDateTime.now())
                .feedCategory(FeedCategory.NOTICE)
                .modified(false)
                .activated(true)
                .build();

        int noticeId = feedRepository.save(feed).getFeedId();
        Feed getAddedFeed = feedRepository.findByFeedId(noticeId);
        assertThat(getAddedFeed).isNotNull();


        //Notice Delete Test
        getAddedFeed.setActivated(false);
        feedRepository.save(getAddedFeed);

        Feed getDeletedFeed = feedRepository.findByFeedId(getAddedFeed.getFeedId());
        assertThat(getDeletedFeed.isActivated()).isFalse();

    }
}
