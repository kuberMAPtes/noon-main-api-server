package com.kube.noon.member.dto;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AddMemberDto {

    private String memberId;

    private Role memberRole;

    private String nickname;

    private String pwd;

    private String phoneNumber;

    private LocalDateTime unlockTime;

    private String profilePhotoUrl;

    private String profileIntro;

    private int dajungScore;

    private boolean signedOff;

    private PublicRange buildingSubscriptionPublicRange;

    private PublicRange allFeedPublicRange;

    private PublicRange memberProfilePublicRange;

    private PublicRange receivingAllNotificationAllowed;









}
