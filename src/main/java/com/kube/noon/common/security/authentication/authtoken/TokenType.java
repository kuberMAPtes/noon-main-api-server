package com.kube.noon.common.security.authentication.authtoken;

/**
 * Client로부터 전해진 Token의 타입
 */
public enum TokenType {
    NATIVE_TOKEN(0),
    KAKAO_TOKEN(1),
    GOOGLE_TOKEN(2);

    private final int code;

    TokenType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}