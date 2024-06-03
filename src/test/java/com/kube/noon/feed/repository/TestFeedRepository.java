package com.kube.noon.feed.repository;

import com.kube.noon.feed.entity.Feed;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest
public class TestFeedRepository {
    @Autowired
    private FeedRepository feedRepository;

    @Test
    public void feedListTest() {
        List<Feed> feedList = feedRepository.findAll();

        assertThat(feedList).isNotNull();
        assertThat(feedList.size()).isGreaterThan(0);

        for(Feed f : feedList) {
            log.info(f.toString());
        }
    }
}
