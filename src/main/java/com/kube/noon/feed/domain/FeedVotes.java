package com.kube.noon.feed.domain;

import com.kube.noon.feed.domain.converter.ListToIntegerConverter;
import com.kube.noon.feed.domain.converter.ListToStringConverter;
import com.kube.noon.feed.domain.converter.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Map;

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

    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Integer> voterIds;

    @OneToOne
    @MapsId
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Override
    public String toString() {
        return "feedId : " + feedId + " question : " + question + " options : " + options + " votes : " + votes + " voterIds : " + voterIds;
    }
}
