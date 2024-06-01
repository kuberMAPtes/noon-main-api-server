package com.kube.noon.building.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "building", indexes = {
        @Index(name = "idx_building_building_name", columnList = "building_name"),
        @Index(name = "idx_building_road_addr", columnList = "road_addr"),
        @Index(name = "idx_building_longitude", columnList = "longitude"),
        @Index(name = "idx_building_latitude", columnList = "latitude")
})
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private int buildingId;

    @Column(name = "building_name", length = 100)
    private String buildingName;

    @Column(name = "profile_activated")
    private boolean profileActivated;

    @Column(name = "road_addr", length = 100)
    private String roadAddr;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "feed_ai_summary", length = 100)
    private String feedAiSummary;
}
