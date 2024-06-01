package com.kube.noon.places.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Place {
    private String placeName;
    private String roadAddress;
    private double latitude;
    private double longitude;
}
