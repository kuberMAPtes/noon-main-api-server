package com.kube.noon.feed.dto;

import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import lombok.*;

import java.util.List;

/**
 * 피드가 수정될 때 사용하는 Dto이다.
 * 정해진 데이터만 수정 가능하도록 한다.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateFeedDto {
    private int feedId;
    private String feedText;
    private String title;
    private PublicRange publicRange;
    private FeedCategory feedCategory;
    private List<String> updateTagList; // 피드를 수정할 때 태그 리스트를 가져올 리스트
}
