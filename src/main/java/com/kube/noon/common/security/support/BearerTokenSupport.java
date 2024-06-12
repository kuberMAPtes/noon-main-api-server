package com.kube.noon.common.security.support;

import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;

/**
 * Bearer Token에 대한 operation을 정의한 인터페이스.
 *
 * @author PGD
 * @see JwtSupport
 */
public interface BearerTokenSupport {

    TokenPair generateToken(String code);

    TokenPair refreshToken(String refreshToken) throws InvalidRefreshTokenException;

    String extractMemberId(String token);

    boolean isTokenExpired(String token);

    boolean isValidRefreshToken(String refreshToken);

    boolean isRefreshToken(String token);

    void invalidateRefreshToken(String refreshToken);

    void invalidateRefreshTokenByMemberId(String memberId);

    boolean supports(TokenType tokenType);
}
