package com.kube.noon.feed.entity;

import com.kube.noon.building.domain.Building;
import com.kube.noon.common.FeedCategory;
import com.kube.noon.common.PublicRange;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feed")
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private int feedId;

    @Column(name = "writer_id", length = 20, nullable = false)
    private String writerId;

    @Column(name = "building_id")
    private int buildingId;

    @Column(name = "main_activated")
    private boolean mainActivated;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_range", nullable = false)
    private PublicRange publicRange;

    @Column(name = "title", length = 40, nullable = false)
    private String title;

    @Column(name = "feed_text", length = 4000, nullable = false)
    private String feedText;

    @Column(name = "view_cnt", nullable = false)
    private Long viewCnt;

    @Column(name = "written_time", nullable = false)
    private LocalDateTime writtenTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "feed_category", nullable = false)
    private FeedCategory feedCategory;

    @Column(name = "modified", nullable = false)
    private boolean modified;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedComment> comments;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TagFeed> tagFeeds;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "building_id", nullable = false)
//    private Building building;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;

    @Override
    public String toString() {
        return "Feed : " + feedId;
    }

}
