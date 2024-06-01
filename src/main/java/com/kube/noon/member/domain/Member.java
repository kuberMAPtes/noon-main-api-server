package com.kube.noon.member.domain;

import com.kube.noon.member.enums.Role;
import com.kube.noon.common.PublicRange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Setter
@Builder
public class Member {

    @Id
    @NonNull
    private String memberId;

    @Column(name = "member_role")
    private Role memberRole;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "profile_intro")
    private String profileIntro;

    @Column(name = "dajung_score")
    private Integer dajungScore;

    @Column(name = "signed_off")
    private Boolean signedOff;

    @Column(name = "building_subscription_public_range")
    private PublicRange buildingSubscriptionPublicRange;

    @Column(name = "all_feed_public_range")
    private PublicRange allFeedPublicRange;

    @Column(name = "member_profile_public_range")
    private PublicRange memberProfilePublicRange;

    @Column(name = "receiving_all_notification_allowed")
    private PublicRange receivingAllNotificationAllowed;




}
