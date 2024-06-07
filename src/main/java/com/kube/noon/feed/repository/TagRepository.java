package com.kube.noon.feed.repository;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import com.kube.noon.feed.dto.TagDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
//    /**
//     * 각 피드에 붙어있는 피드의 내용을 가져온다.
//     * @param feed
//     * @return
//     */
//    @Query("""
//        SELECT t
//        FROM TagFeed f
//                 INNER JOIN Tag t ON f.tag.tagId = t.tagId
//        WHERE f.feed.feedId = :#{#feed.feedId}
//        """)
//    List<Tag> getTagByFeedId(@Param("feed") Feed feed);

     /**
      * 각 피드에 붙어있는 피드의 내용을 가져온다.
      * @param feed
      * @return
      */
     @Query("""
         SELECT t
         FROM Tag t
                  INNER JOIN TagFeed f ON t.tagId = f.tag.tagId
         WHERE f.feed.feedId = :#{#feed.feedId}
         """)
     List<Tag> getTagByFeedId(@Param("feed") Feed feed);

    /**
     * 태그 택스트로 태그를 찾는다.
     * @param tagText
     * @return
     */
    Tag findByTagText(String tagText);
}
