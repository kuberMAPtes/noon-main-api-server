package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedComment;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
public class TestFeedCommentRepository {

    @Autowired
    private FeedCommentRepository feedCommentRepository;

    /**
     * 하나의 피드에 대한 댓글을 가져온다.
     * feed_id = 10000을 기준으로 가져온다.
     */
    @Transactional
    @Test
    public void getFeedComments() {
        Feed feed = Feed.builder().feedId(10000).build();
        List<FeedComment> feedComments = feedCommentRepository.findByFeed(feed);

        // test 1) 정상적으로 가져옴을 확인
        assertThat(feedComments).isNotNull();
        assertThat(feedComments.size()).isGreaterThan(0);

        for (FeedComment f : feedComments) {
            log.info(f);
        }
    }

    /**
     * 하나의 피드에 댓글을 추가한다.
     * commenterId = "member_1", feed_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void addFeedCommentTest() {
        Feed feed = Feed.builder().feedId(10000).build();
        FeedComment feedComment = FeedComment.builder()
                .commenterId("member_1")
                .commentText("집에 가고 싶다.")
                .writtenTime(LocalDateTime.now())
                .activated(true)
                .feed(feed)
                .build();

        // test 1) 정상 저장 확인
        assertThat(feedCommentRepository.save(feedComment)).isNotNull();
        log.info(feedComment);
    }

    /**
     * 하나의 피드의 댓긍을 삭제한다 (= 노출 중지한다.)
     * feed_id = 10000, commenter_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void deleteFeedCommentTest() {
        FeedComment feedComment = feedCommentRepository.findByCommentId(10000);
        feedComment.setActivated(false);

        assertThat(feedCommentRepository.save(feedComment).isActivated()).isEqualTo(false);
    }
}
