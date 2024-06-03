package com.kube.noon.common;

/**
 * Feed, Building subscription, Member profile의 공개범위를 정의한 enum
 *
 * @author PGD
 */
public enum PublicRange {
    PUBLIC,
    FOLLOWER_ONLY,
    MUTUAL_ONLY,
    PRIVATE
}
