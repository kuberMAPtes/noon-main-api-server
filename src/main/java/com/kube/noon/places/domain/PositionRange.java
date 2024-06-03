package com.kube.noon.places.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 직사각형 형태의 위치범위를 표현하는 객체
 */
@Getter
@ToString
public class PositionRange {
    private final Position leftBottom;
    private final Position leftUp;
    private final Position rightBottom;
    private final Position rightUp;

    /**
     * PositionRange 생성자
     * @param leftBottom 왼쪽 아래 좌표
     * @param leftUp 왼쪽 위 좌표
     * @param rightBottom 오른쪽 아래 좌표
     * @param rightUp 오른쪽 위 좌표
     */
    public PositionRange(Position leftBottom, Position leftUp, Position rightBottom, Position rightUp) {
        this.leftBottom = leftBottom;
        this.leftUp = leftUp;
        this.rightBottom = rightBottom;
        this.rightUp = rightUp;
    }
}
