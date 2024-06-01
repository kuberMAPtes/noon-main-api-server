package com.kube.noon.common.zzim;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "zzim")
@NoArgsConstructor
@AllArgsConstructor
public class Zzim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zzim_id")
    private int zzimId;

    @Column(name = "member_id", nullable = false, length = 20)
    private String memberId;

    @Column(name = "feed_id")
    private int feedId;

    @Column(name = "building_id", nullable = false)
    private int buildingId;

    @Column(name = "subscription_provider_id", nullable = false, length = 20)
    private String subscriptionProviderId;

    @Column(name = "zzim_type")
    private String zzimType;

    @Column(name = "activated")
    private boolean activated;
}
