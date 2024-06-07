package com.kube.noon.feed.dto;

import lombok.*;

/**
 * MyBatis Mapping VO : "태그별 게시물 수" 사용 시 Mapping함
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedCntByTagDto {
    private String tagText;
    private int count;
}
