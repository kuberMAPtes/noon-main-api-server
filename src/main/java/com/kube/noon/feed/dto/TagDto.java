package com.kube.noon.feed.dto;

import com.kube.noon.feed.domain.Feed;
import com.kube.noon.feed.domain.Tag;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TagDto {
    private int tagId;
    private String tagText;

    public static TagDto toDto(Tag tag) {
        return TagDto.builder()
                .tagId(tag.getTagId())
                .tagText(tag.getTagText())
                .build();
    }

    public static List<TagDto> toDtoList(List<Tag> tagList) {
        if(tagList == null || tagList.isEmpty()) {
            return null;
        } else {
            return tagList.stream().map(TagDto::toDto).collect(Collectors.toList());
        }
    }
}
