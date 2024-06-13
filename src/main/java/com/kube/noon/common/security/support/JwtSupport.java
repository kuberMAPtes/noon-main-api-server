package com.kube.noon.common.security.support;

import com.kube.noon.common.security.TokenPair;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.support.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT를 지원하는 BearerTokenSupport 구현체.
 * JWT Access Token, Refresh Token 생성,
 * JWT 검증을 담당한다.
 *
 * @author PGD
 * @see BearerTokenSupport
 */
@Slf4j
@Component
public class JwtSupport implements BearerTokenSupport {
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final String REFRESH_TOKEN_CLAIM = "refreshToken";
    private static final String ISSUER = "noon";
    private static final JwtParser jwtParser = Jwts.parser()
            .requireIssuer(ISSUER)
            .verifyWith(SECRET_KEY)
            .build();

    private final RefreshTokenRepository refreshTokenRepository;
    private final long ACCESS_TOKEN_DURATION_IN_MILLIS; // 5분
    private final long REFRESH_TOKEN_DURATION_IN_MILLIS; // 일주일

    public JwtSupport(RefreshTokenRepository refreshTokenRepository,
                      @Value("${jwt.access-token.duration-time-in-mills}") long accessTokenDurationInMills,
                      @Value("${jwt.refresh-token.duration-time-in-mills}") long refreshTokenDurationInMills) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.ACCESS_TOKEN_DURATION_IN_MILLIS = accessTokenDurationInMills;
        this.REFRESH_TOKEN_DURATION_IN_MILLIS = refreshTokenDurationInMills;
    }

    @Override
    public TokenPair generateToken(String memberId) {
        return new TokenPair(generateAccessToken(memberId), generateRefreshToken(memberId));
    }

    @Override
    public TokenPair refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        if (refreshToken == null || !isValidRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token=" + refreshToken);
        }
        String memberId = extractMemberId(refreshToken);
        return new TokenPair(generateAccessToken(memberId), generateRefreshToken(memberId));
    }

    private String generateAccessToken(String memberId) {
        return generateToken(memberId, false);
    }

    private String generateRefreshToken(String memberId) {
        invalidateRefreshTokenByMemberId(memberId);
        String token = generateToken(memberId, true);
        this.refreshTokenRepository.save(memberId, token);
        return token;
    }

    private String generateToken(String memberId, boolean refreshToken) {
        Date now = new Date();
        log.trace("now={}", now);
        Date expiration = new Date(
                now.getTime()
                        + (refreshToken ? REFRESH_TOKEN_DURATION_IN_MILLIS : ACCESS_TOKEN_DURATION_IN_MILLIS)
        );
        log.trace("expiration={}", expiration);
        return Jwts.builder()
                .subject(memberId)
                .issuedAt(now)
                .issuer(ISSUER)
                .expiration(expiration)
                .claim(REFRESH_TOKEN_CLAIM, refreshToken)
                .signWith(SECRET_KEY)
                .compact();
    }

    @Override
    public String extractMemberId(String token) throws JwtException {
        return getPayloads(token).getSubject();
    }

    private Claims getPayloads(String token) throws JwtException {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return getPayloads(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            Claims payloads = getPayloads(refreshToken);
            return isRefreshToken(payloads)
                    && isRefreshTokenRetrievable(payloads, refreshToken)
                    && !isTokenExpired(refreshToken);
        } catch (JwtException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isRefreshToken(Claims payloads) {
        log.debug("payloads={}", payloads);
        return payloads.get(REFRESH_TOKEN_CLAIM, Boolean.class);
    }

    @Override
    public boolean isRefreshToken(String token) throws JwtException {
        return getPayloads(token).get(REFRESH_TOKEN_CLAIM, Boolean.class);
    }

    private boolean isRefreshTokenRetrievable(Claims payloads, String token) {
        log.debug("payloads={}", payloads);
        log.debug("token={}", token);
        String subject = payloads.getSubject();
        return this.refreshTokenRepository.exists(subject, token);
    }

    @Override
    public void invalidateRefreshToken(String refreshToken) throws JwtException {
        this.refreshTokenRepository.remove(extractMemberId(refreshToken));
    }

    @Override
    public void invalidateRefreshTokenByMemberId(String memberId) {
        this.refreshTokenRepository.remove(memberId);
    }

    @Override
    public boolean supports(TokenType tokenType) {
        return tokenType == TokenType.NATIVE_TOKEN;
    }
}
