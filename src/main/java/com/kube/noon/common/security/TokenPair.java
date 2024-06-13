package com.kube.noon.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class TokenPair {
    private final String accessToken;
    private final String refreshToken;
}
