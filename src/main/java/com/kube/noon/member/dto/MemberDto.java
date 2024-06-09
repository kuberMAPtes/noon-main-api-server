package com.kube.noon.member.dto;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.enums.Role;
import lombok.*;

import java.time.LocalDateTime;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private String memberId;

    private Role memberRole;

    private String nickname;

    private String pwd;

    private String phoneNumber;

    private LocalDateTime unlockTime;

    private String profilePhotoUrl;//

    private String profileIntro;//

    private Integer dajungScore;

    private Boolean signedOff;

    private PublicRange buildingSubscriptionPublicRange;

    private PublicRange allFeedPublicRange;

    private PublicRange memberProfilePublicRange;

    private Boolean receivingAllNotificationAllowed;

}
