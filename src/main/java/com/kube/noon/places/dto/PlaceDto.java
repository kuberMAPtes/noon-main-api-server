package com.kube.noon.places.dto;

import com.kube.noon.places.domain.Place;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
public class PlaceDto {
    private String placeName;
    private String roadAddress;
    private double latitude;
    private double longitude;

    private PlaceDto() {
    }

    public static PlaceDto from(Place place) {
        return PlaceDto.builder()
                .placeName(place.getPlaceName())
                .roadAddress(place.getRoadAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }
}
