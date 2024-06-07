package com.kube.noon.member.RequestDto;

import com.kube.noon.member.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class GetMemberRequestDto {

    private String memberId;
    private Role memberRole;
    private String nickname;
    private String pwd;
    private String phoneNumber;
    private String LocalDateTime;
    private String profilePhotoUrl;
    private String profileIntro;
    private Integer dajungScore;
    private Boolean signedOff;
    private String buildingSubscriptionPublicRange;
    private String allFeedPublicRange;
    private String memberProfilePublicRange;
    private Boolean receivingAllNotificationAllowed;

}
