package com.kube.noon.member.dto.member;

import com.kube.noon.feed.dto.FeedSummaryDto;
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

    private List<FeedSummaryDto> feedDtoList;

}
