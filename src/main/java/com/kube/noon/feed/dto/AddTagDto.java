package com.kube.noon.feed.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
/**
 * 태그 삽입, 삭제 시 사용
 */
public class AddTagDto {
    private int feedId;
    private String tagText;
}
