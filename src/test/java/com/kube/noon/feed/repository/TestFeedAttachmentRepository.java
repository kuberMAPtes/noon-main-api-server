package com.kube.noon.feed.repository;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.FeedAttachment;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
public class TestFeedAttachmentRepository {

    @Autowired
    private FeedAttachmentRepository feedAttachmentRepository;

    /**
     * 피드의 첨부파일을 가져온다.
     * feed_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void getAttachmentTest() {
        Feed feed = Feed.builder().feedId(10000).build();
        List<FeedAttachment> attachments = feedAttachmentRepository.findByFeed(feed);

        // test 1) 개수 확인
        assertThat(attachments.size()).isGreaterThan(0);
        log.info(attachments.size());

        // test 2) console printing
        // attachments.stream().forEach(System.out::println);
    }

    /**
     * 피드에 첨부파일을 넣는다.
     * feed_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void addAttachmentTest() {
        Feed feed = Feed.builder().feedId(10000).build();
        FeedAttachment feedAttachment = FeedAttachment.builder()
                .feed(feed)
                .fileUrl("https://example.com/file_test.jpg")
                .fileType(FileType.PHOTO)
                .blurredFileUrl(null)
                .activated(true)
                .build();

        // test 1) 삽입 확인
        assertThat(feedAttachmentRepository.save(feedAttachment)).isNotNull();
        log.info(feedAttachment);
    }

    /**
     * 피드에 첨부파일을 하나 삭제한다.
     * feed_id = 10000, attachment_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void deleteAttachmentTest() {
        FeedAttachment feedAttachment = feedAttachmentRepository.findByAttachmentId(10000);
        feedAttachment.setActivated(false);
        
        // test 1) 수정 확인
        assertThat(feedAttachmentRepository.save(feedAttachment)).isNotNull();
        log.info(feedAttachment);
    }
}
