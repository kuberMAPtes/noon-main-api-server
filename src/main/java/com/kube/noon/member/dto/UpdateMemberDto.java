package com.kube.noon.member.dto;

import com.kube.noon.common.PublicRange;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UpdateMemberDto {

    private String memberId;

//    private String memberRole;

    private String pwd;

    private String nickname;

//    private String pwd;

    private String phoneNumber;

    private LocalDateTime unlockTime;

    private String profilePhotoUrl;

    private String profileIntro;

    private int dajungScore;

//    private Boolean signedOff;

    private PublicRange buildingSubscriptionPublicRange;

    private PublicRange allFeedPublicRange;

    private PublicRange memberProfilePublicRange;

    private boolean receivingAllNotificationAllowed;


}
