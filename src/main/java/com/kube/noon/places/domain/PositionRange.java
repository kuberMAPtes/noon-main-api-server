package com.kube.noon.places.domain;

import lombok.*;

/**
 * 직사각형 형태의 위치범위를 표현하는 객체
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class PositionRange {
    private double lowerLatitude;
    private double lowerLongitude;
    private double upperLatitude;
    private double upperLongitude;
}
