package com.kube.noon.places.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 위도, 경도 좌표를 나타내는 객체
 */
@AllArgsConstructor
@Getter
@ToString
public class Position {
    private final double latitude;
    private final double longitude;
}
