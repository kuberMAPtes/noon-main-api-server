package com.kube.noon.feed.service;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.domain.FeedAttachment;
import com.kube.noon.feed.dto.FeedAttachmentDto;
import com.kube.noon.feed.repository.FeedAttachmentRepository;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.feed.service.impl.FeedServiceImpl;
import com.kube.noon.feed.service.impl.FeedSubServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
@ActiveProfiles("winterhana")
public class TestFeedSubServiceImpl {
    @Autowired
    private FeedServiceImpl feedServiceImpl;

    @Autowired
    private FeedSubServiceImpl feedSubServiceImpl;
    @Autowired
    private FeedAttachmentRepository feedAttachmentRepository;

    // -------------- 1. 피드 첨부 파일 관련 테스트 --------------
    /**
     * 피드 첨부파일을 가져오는 것에 대한 테스트를 진행한다.
     * feed_id = 10001을 기준으로 진행한다.
     */
    @Transactional
    @Test
    public void getFeedAttachmentTest() {
        List<FeedAttachmentDto> feedAttachmentList = feedSubServiceImpl.getFeedAttachmentList(10001);

        assertThat(feedAttachmentList).isNotNull();
        assertThat(feedAttachmentList.isEmpty()).isFalse();
        assertThat(feedAttachmentList.size()).isGreaterThan(0);
    }

    /**
     * 피드에 첨부 파일을 추가한다.
     * feed_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void addFeedAttachmentTest() {
        FeedAttachmentDto feedAttachmentDto = FeedAttachmentDto.builder()
                .feedId(10000)
                .fileUrl("https://example.com/file_test.jpg")
                .fileType(FileType.PHOTO)
                .blurredFileUrl(null)
                .activated(true)
                .build();

        int attachmentId = feedSubServiceImpl.addFeedAttachment(feedAttachmentDto);

        FeedAttachmentDto getFeedAttachmentDto = feedSubServiceImpl.getFeedAttachment(attachmentId);

        assertThat(getFeedAttachmentDto).isNotNull();
        assertThat(getFeedAttachmentDto.getFeedId()).isEqualTo(10000);
        assertThat(getFeedAttachmentDto.getFileUrl()).isEqualTo("https://example.com/file_test.jpg");
    }

    /**
     * 정해진 첨부파일 번호를 통해 첨부파일을 삭제한다.
     * attachment_id = 10000을 기준으로 한다.
     */
    @Transactional
    @Test
    public void deleteAttachmentTest() {
        int attachmentId = feedSubServiceImpl.deleteFeedAttachment(10000);

        FeedAttachment getFeedAttachment = feedAttachmentRepository.findByAttachmentId(attachmentId);

        assertThat(getFeedAttachment.isActivated()).isFalse();
    }

    /**
     * 원하는 타입의 첨부파일을 가져온다
     * 여기서는 FileType.PHOTO를 기준으로 한다.
     */
    @Transactional
    @Test
    public void getFeedAttachementListByFileTypeTest() {
        List<FeedAttachmentDto> feedAttachmentDtoList = feedSubServiceImpl.getFeedAttachementListByFileType(FileType.PHOTO);

        assertThat(feedAttachmentDtoList).isNotNull();
        assertThat(feedAttachmentDtoList).isNotEmpty();
        assertThat(feedAttachmentDtoList.size()).isGreaterThan(0);

        for (FeedAttachmentDto feedAttachmentDto : feedAttachmentDtoList) {
            log.info(feedAttachmentDto);
        }
    }
}
