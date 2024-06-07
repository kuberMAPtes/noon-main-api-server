package com.kube.noon.feed.dto;

import com.kube.noon.common.zzim.Zzim;
import com.kube.noon.common.zzim.ZzimType;
import com.kube.noon.feed.domain.Feed;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeedBookmarkAndLikeDto {
    private int zzimId;
    private String memberId;
    private int feedId;
    private int buildingId;
    private ZzimType zzimType;

    public static FeedBookmarkAndLikeDto toDto(Zzim zzim) {
        return FeedBookmarkAndLikeDto.builder()
                .zzimId(zzim.getZzimId())
                .memberId(zzim.getMemberId())
                .feedId(zzim.getFeedId())
                .buildingId(zzim.getBuildingId())
                .zzimType(zzim.getZzimType())
                .build();
    }

    public static Zzim toEntity(FeedBookmarkAndLikeDto feedBookmarkAndLikeDto) {
        return Zzim.builder()
                .zzimId(feedBookmarkAndLikeDto.zzimId)
                .memberId(feedBookmarkAndLikeDto.memberId)
                .feedId(feedBookmarkAndLikeDto.feedId)
                .buildingId(feedBookmarkAndLikeDto.buildingId)
                .zzimType(feedBookmarkAndLikeDto.zzimType)
                .build();
    }

    public static List<FeedBookmarkAndLikeDto> toDtoList(List<Zzim> feeds) {
        return feeds.stream().map(FeedBookmarkAndLikeDto::toDto).collect(Collectors.toList());
    }
}
