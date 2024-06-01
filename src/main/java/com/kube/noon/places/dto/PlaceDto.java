package com.kube.noon.places.dto;

import com.kube.noon.places.domain.Place;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@ToString
public class PlaceDto {
    private String placeName;
    private String roadAddress;
    private double x;
    private double y;

    private PlaceDto() {
    }

    public static PlaceDto from(Place place) {
        return PlaceDto.builder()
                .placeName(place.getPlaceName())
                .roadAddress(place.getRoadAddress())
                .x(place.getX())
                .y(place.getY())
                .build();
    }
}
