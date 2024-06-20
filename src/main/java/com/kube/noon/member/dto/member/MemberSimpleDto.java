package com.kube.noon.member.dto.member;


import com.kube.noon.feed.dto.FeedSummaryDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MemberSimpleDto {

    @NonNull
    private String memberId;

    private String nickname;

    private String profilePhotoUrl;


}
