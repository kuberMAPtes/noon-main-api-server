package com.kube.noon.feed.service;

import com.kube.noon.common.FileType;
import com.kube.noon.feed.dto.FeedAttachmentDto;
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
