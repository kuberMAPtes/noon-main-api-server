package com.kube.noon.common.security.support;

/**
 * Bearer Token에 대한 operation을 정의한 인터페이스.
 *
 * @author PGD
 * @see JwtSupport
 */
public interface BearerTokenSupport {

    String generateAccessToken(String memberId);

    String generateRefreshToken(String memberId);

    String extractMemberId(String token);

    boolean isTokenExpired(String token);

    boolean isValidRefreshToken(String refreshToken);

    boolean isRefreshToken(String token);

    void invalidateRefreshToken(String refreshToken);

    void invalidateRefreshTokenByMemberId(String memberId);
}
