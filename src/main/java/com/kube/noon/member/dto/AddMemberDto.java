package com.kube.noon.member.dto;

import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AddMemberDto {

    private String memberId;

    private String memberRole;

    private String nickname;

    private String pwd;

    private String phoneNumber;

    private String unlockTime;

    private String profilePhotoUrl;

    private String profileIntro;

    private Integer dajungScore;

    private Boolean signedOff;

    private String buildingSubscriptionPublicRange;

    private String allFeedPublicRange;

    private String memberProfilePublicRange;

    private String receivingAllNotificationAllowed;









}
