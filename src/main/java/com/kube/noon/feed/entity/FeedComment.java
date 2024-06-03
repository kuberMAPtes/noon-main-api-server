package com.kube.noon.feed.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feed_comment")
public class FeedComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

//    @Column(name = "feed_id", nullable = false)
//    private int feedId;

    @Column(name = "commenter_id", nullable = false, length = 20)
    private String commenterId;

    @Column(name = "comment_text", nullable = false, length = 4000)
    private String commentText;

    @Column(name = "written_time", nullable = false)
    private LocalDateTime writtenTime;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id" nullable = false)
//    private Member member;
}

