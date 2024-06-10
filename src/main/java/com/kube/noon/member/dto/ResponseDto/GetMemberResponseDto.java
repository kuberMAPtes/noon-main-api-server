package com.kube.noon.member.dto.ResponseDto;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
public class GetMemberResponseDto {

    @Id
    @Column(name = "member_id", length = 20)
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, columnDefinition = "ENUM('MEMBER','ADMIN') default 'MEMBER'")
    private Role memberRole = Role.MEMBER;

    @Column(name = "nickname", length = 30, unique = true, nullable = false)
    private String nickname;

    @Column(name = "pwd", length = 5000, nullable = false)
    private String pwd;

    @Column(name = "phone_number", length = 20, unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "unlock_time", columnDefinition = "DATETIME DEFAULT '0001-01-01 01:01:01'")
    private LocalDateTime unlockTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);

    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhotoUrl;

    @Column(name = "profile_intro", length = 200)
    private String profileIntro;

    @Column(name = "dajung_score", nullable = false, columnDefinition = "INT default 0")
    private Integer dajungScore = 0;

    @Column(name = "signed_off", nullable = false, columnDefinition = "BOOLEAN default FALSE")
    private Boolean signedOff = false;

    private PublicRange buildingSubscriptionPublicRange;

    private PublicRange allFeedPublicRange;

    private PublicRange memberProfilePublicRange;

    private Boolean receivingAllNotificationAllowed;

}
