package com.kube.noon.feed.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feed_event")
public class FeedEvent {

    @Id
    @Column(name = "feed_id")
    private int feedId;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
}
