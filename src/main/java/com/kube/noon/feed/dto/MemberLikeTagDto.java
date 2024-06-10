package com.kube.noon.feed.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MemberLikeTagDto {
    private String memberId;
    private String tagText;
    private double tagCount;
}
