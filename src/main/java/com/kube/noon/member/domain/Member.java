package com.kube.noon.member.domain;

import com.kube.noon.common.PublicRange;
import com.kube.noon.member.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "members")
public class Member {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "building_subscription_public_range", columnDefinition = "ENUM('PUBLIC','FOLLOWER_ONLY','MUTUAL_ONLY','PRIVATE')")
    private PublicRange buildingSubscriptionPublicRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "all_feed_public_range", columnDefinition = "ENUM('PUBLIC','FOLLOWER_ONLY','MUTUAL_ONLY','PRIVATE')")
    private PublicRange allFeedPublicRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_profile_public_range", columnDefinition = "ENUM('PUBLIC','FOLLOWER_ONLY','MUTUAL_ONLY','PRIVATE')")
    private PublicRange memberProfilePublicRange;

    @Column(name = "receiving_all_notification_allowed", nullable = false, columnDefinition = "BOOLEAN default FALSE")
    private Boolean receivingAllNotificationAllowed;
}