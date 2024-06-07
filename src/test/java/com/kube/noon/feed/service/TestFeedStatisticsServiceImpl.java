package com.kube.noon.feed.service;

import com.kube.noon.feed.dto.FeedCntByTagDto;
import com.kube.noon.feed.dto.FeedPopularityDto;
import com.kube.noon.feed.dto.FeedViewCntByBuildingDto;
import com.kube.noon.feed.service.impl.FeedStatisticsServiceImpl;
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
public class TestFeedStatisticsServiceImpl {
    @Autowired
    private FeedStatisticsServiceImpl feedStatisticsServiceImpl;

    /**
     * 건물별로 조회수가 높은 피드를 가져온다. (상위 5개)
     */
    @Transactional
    @Test
    public void getFeedViewCntByBuildingTest() {
        List<FeedViewCntByBuildingDto> result = feedStatisticsServiceImpl.getFeedViewCntByBuilding(10001);

        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(0);
        for (FeedViewCntByBuildingDto feedViewCntByBuildingDto : result) {
            log.info(feedViewCntByBuildingDto);
        }
    }

    /**
     * 피드에 많이 사용된 태그들을 가져온다. (상위 5개)
     */
    @Transactional
    @Test
    public void getFeedCntByTagTest() {
        List<FeedCntByTagDto> result = feedStatisticsServiceImpl.getFeedCntByTag();

        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(0);
        for (FeedCntByTagDto feedCntByTagDto : result) {
            log.info(feedCntByTagDto);
        }
    }

    /**
     * 건물별로 인기있는 피드를 가져온다. (상위 5개)
     */
    @Transactional
    @Test
    public void getFeedPopularityTest() {
        List<FeedPopularityDto> result = feedStatisticsServiceImpl.getFeedPopularity(10001);

        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(0);
        for (FeedPopularityDto feedPopularityDto : result) {
            log.info(feedPopularityDto);
        }
    }
}
