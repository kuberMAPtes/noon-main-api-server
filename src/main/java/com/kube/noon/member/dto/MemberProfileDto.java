package com.kube.noon.member.dto;

import com.kube.noon.common.PublicRange;
import com.kube.noon.feed.dto.FeedDto;
import lombok.*;

import java.util.List;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class MemberProfileDto {

    @NonNull
    private String memberId;

    private String nickname;

    private String profilePhotoUrl;

    private String profileIntro;

    private Integer dajungScore;

    private Boolean signedOff;

    private List<FeedDto> feedDtoList;

    private List<FeedDto> feedDtoList;

    private PublicRange buildingSubscriptionPublicRange;

    private PublicRange allFeedPublicRange;

    private PublicRange memberProfilePublicRange;

}
