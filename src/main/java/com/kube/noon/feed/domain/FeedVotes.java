package com.kube.noon.feed.domain;

import com.kube.noon.feed.domain.converter.ListToIntegerConverter;
import com.kube.noon.feed.domain.converter.ListToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feed_votes")
public class FeedVotes {
    @Id
    private int feedId;

    private String question;

    @Convert(converter = ListToStringConverter.class)
    private List<String> options;

    @Convert(converter = ListToIntegerConverter.class)
    private List<Integer> votes;

    @OneToOne
    @MapsId
    @JoinColumn(name = "feed_id")
    private Feed feed;
}
