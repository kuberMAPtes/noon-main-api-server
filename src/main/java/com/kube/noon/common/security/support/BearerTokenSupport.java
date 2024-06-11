package com.kube.noon.common.security.support;

public interface BearerTokenSupport {

    String generateAccessToken(String memberId);

    String generateRefreshToken(String memberId);

    String extractMemberId(String token);

    boolean isTokenExpired(String token);

    boolean isValidRefreshToken(String refreshToken);

    void invalidateRefreshToken(String refreshToken);

    void invalidateRefreshTokenByMemberId(String memberId);
}
