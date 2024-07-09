package com.kube.noon.common.zzim;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "zzim")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Zzim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zzim_id")
    private int zzimId;

    @Column(name = "member_id", nullable = false, length = 20)
    private String memberId;

    @Column(name = "feed_id")
    private Integer feedId;

    @Column(name = "building_id")
    private Integer buildingId;

    @Column(name = "subscription_provider_id", length = 20)
    private String subscriptionProviderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "zzim_type", columnDefinition = "ENUM('LIKE','BOOKMARK','SUBSCRIPTION')")
    private ZzimType zzimType;

    @Column(name = "activated")
    private boolean activated;

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
