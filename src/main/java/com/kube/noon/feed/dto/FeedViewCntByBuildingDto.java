package com.kube.noon.feed.dto;

import lombok.*;

/**
 * MyBatis Mapping VO : "건물별 조회수가 높은 게시물 상위 5개를 가져온다." 사용 시 Mapping함
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedViewCntByBuildingDto {
    private int feedId;
    private String title;
    private int viewCnt;
}
