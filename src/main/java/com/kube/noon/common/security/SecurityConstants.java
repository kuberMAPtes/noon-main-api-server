package com.kube.noon.common.security;

public enum SecurityConstants {
    ACCESS_TOKEN_COOKIE_KEY("token"),
    REFRESH_TOKEN_COOKIE_KEY("refresh_token"),
    TOKEN_TYPE_COOKIE_KEY("token_type");

    private final String value;

    SecurityConstants(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
}
