package com.kube.noon.common.security.authentication.authtoken;

/**
 * Client로부터 전해진 Token의 타입
 */
public enum TokenType {
    NATIVE_TOKEN,
    KAKAO_TOKEN,
    GOOGLE_TOKEN
}
