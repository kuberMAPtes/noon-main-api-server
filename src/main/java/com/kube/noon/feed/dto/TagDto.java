package com.kube.noon.feed.dto;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TagDto {
    private int tagId;
    private String tagText;
}
