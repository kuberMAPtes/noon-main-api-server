package com.kube.noon.member.dto;

import com.kube.noon.common.PublicRange;
import lombok.*;

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

//    private String memberRole;

    private String nickname;

//    private String pwd;

//    private String phoneNumber;

//    private String unlockTime;

    private String profilePhotoUrl;

    private String profileIntro;

    private int dajungScore;

    private boolean signedOff;

    private PublicRange buildingSubscriptionPublicRange;

    private PublicRange allFeedPublicRange;

    private PublicRange memberProfilePublicRange;

//    private boolean receivingAllNotificationAllowed;

}
