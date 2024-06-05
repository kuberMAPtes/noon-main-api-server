package com.kube.noon.feed.repository;

import com.kube.noon.feed.dto.TagDto;
import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import com.kube.noon.feed.domain.TagFeed;
import com.kube.noon.feed.repository.mybatis.TagMyBatisRepository;
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
public class TestTagFeedRepository {

    @Autowired
    private TagFeedRepository tagFeedRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMyBatisRepository tagMyBatisRepository;

    /**
     * 피드와 연관있는 태그를 가져온다.
     * feed_id = 10000을 기준으로 테스트한다.
     * Query가 JPA로 표현하기에 복잡하기 때문에 MyBatis Repository를 사용한다.
     */
    @Transactional
    @Test
    public void getFeedTagsTest() {
        List<TagDto> getTagByFeedId = tagMyBatisRepository.getTagByFeedId(10000);

        // test 1) 존재 여부 확인
        assertThat(getTagByFeedId).isNotNull();
        log.info(getTagByFeedId);
    }

    /**
     * 태그 하나를 추가하는 과정을 테스트한다.
     * feed_id = 10005를 기준으로 삼는다.
     */
    @Transactional
    @Test
    public void addTagTest() {
        // 삽입할 태그 이름
        String tagText = "흠흐밍";

        // 1. 삽입 전 tag 테이블에 존재하는지 탐색
        Tag tag = tagRepository.findByTagText(tagText);

        // 1-1. 만약 테이블에 없다면 삽입하기
        if(tag == null) {
            tag = Tag.builder().tagText(tagText).build();
            tagRepository.save(tag);
        }

        // 2. tag_feed 테이블에 추가하기
        Feed feed = Feed.builder().feedId(10005).build();
        TagFeed tagFeed = TagFeed.builder().feed(feed).tag(tag).build();

        // test 1) 삽입 여부 확인
        assertThat(tagFeedRepository.save(tagFeed)).isEqualTo(tagFeed);
        log.info(tagFeed);
    }

    /**
     * 태그 하나를 삭제하는 과정을 테스트한다.
     * feed_id = 10005, tag_id = 맛있다_6 을 기준으로 한다.
     */
    @Transactional
    @Test
    public void deleteTagTest() {
        String tagText = "따듯함";

        // 1. 태그 텍스트에 대한 번호를 가져온다.
        Tag tag = tagRepository.findByTagText(tagText);

        // 1-1. 만약 찾아서 없으면 종료
        if(tag == null) {
            return;
        }

        // 2. 피드에 대한 태그를 삭제한다.
        // test 1) 정상 삭제 확인
        assertThat(tagFeedRepository.deleteByTag(tag)).isEqualTo(1);
    }
}
