package com.kube.noon.common.security.support;

import com.kube.noon.common.security.support.repository.InMemoryRefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles({ "key", "privpark", "dev" })
class TestJwtSupport {
    static final long ACCESS_TOKEN_DURATION_IN_MILLIS = 1000 * 5;
    static final long REFRESH_TOKEN_DURATION_IN_MILLIS = 1000 * 60 * 60 * 24 * 7;
    static final String SAMPLE_MEMBER_ID = "sample-member";

    JwtSupport jwtSupport;

    @BeforeEach
    void beforeEach() {
        this.jwtSupport = new JwtSupport(
                new InMemoryRefreshTokenRepository(),
                ACCESS_TOKEN_DURATION_IN_MILLIS,
                REFRESH_TOKEN_DURATION_IN_MILLIS
        );
    }

    @DisplayName("Access Token 생성 확인")
    @Test
    void generateAccessToken() {
        String token = this.jwtSupport.generateAccessToken(SAMPLE_MEMBER_ID);
        log.info("token={}", token);
        assertThat(token).isNotNull();
    }

    @DisplayName("Refresh Token 생성 확인")
    @Test
    void generateRefreshToken() {
        String token = this.jwtSupport.generateRefreshToken(SAMPLE_MEMBER_ID);
        log.info("token={}", token);
        assertThat(token).isNotNull();
    }

    @DisplayName("Member ID 추출 - Access Token")
    @Test
    void extractMemberId_accessToken() {
        String token = this.jwtSupport.generateAccessToken(SAMPLE_MEMBER_ID);
        log.info("token={}", token);
        String extracted = this.jwtSupport.extractMemberId(token);
        log.info("extracted={}", extracted);
        assertThat(extracted).isEqualTo(SAMPLE_MEMBER_ID);
    }

    @DisplayName("Member ID 추출 - Refresh Token")
    @Test
    void extractMemberId_refreshToken() {
        String token = this.jwtSupport.generateRefreshToken(SAMPLE_MEMBER_ID);
        log.info("token={}", token);
        String extracted = this.jwtSupport.extractMemberId(token);
        log.info("extracted={}", extracted);
        assertThat(extracted).isEqualTo(SAMPLE_MEMBER_ID);
    }

    @DisplayName("토큰이 만료되지 않음 - Access Token")
    @Test
    void checkExpirationOfUnexpiredToken_accessToken() {
        String token = this.jwtSupport.generateAccessToken(SAMPLE_MEMBER_ID);
        assertThat(this.jwtSupport.isTokenExpired(token)).isFalse();
    }

    @DisplayName("토큰 만료 - Access Token")
    @Test
    void checkExpiration_accessToken() throws InterruptedException {
        this.jwtSupport = new JwtSupport(
                new InMemoryRefreshTokenRepository(),
                50,
                50
        );

        String token = this.jwtSupport.generateAccessToken(SAMPLE_MEMBER_ID);
        Thread.sleep(100);
        assertThat(this.jwtSupport.isTokenExpired(token)).isTrue();
    }

    @DisplayName("토큰이 만료되지 않음 - Refresh Token")
    @Test
    void checkExpirationOfUnexpiredToken_refreshToken() {
        String token = this.jwtSupport.generateRefreshToken(SAMPLE_MEMBER_ID);
        assertThat(this.jwtSupport.isTokenExpired(token)).isFalse();
    }

    @DisplayName("토큰 만료 - Refresh Token")
    @Test
    void checkExpiration_refreshToken() throws InterruptedException {
        this.jwtSupport = new JwtSupport(
                new InMemoryRefreshTokenRepository(),
                50,
                50
        );

        String token = this.jwtSupport.generateRefreshToken(SAMPLE_MEMBER_ID);
        Thread.sleep(100);
        assertThat(this.jwtSupport.isTokenExpired(token)).isTrue();
    }

    @DisplayName("Refresh Token 재발급")
    @Test
    void generateRefreshToken_twice() throws InterruptedException {
        String firstToken = this.jwtSupport.generateRefreshToken(SAMPLE_MEMBER_ID);
        assertThat(this.jwtSupport.isValidRefreshToken(firstToken)).isTrue();
        Thread.sleep(500);
        String secondToken = this.jwtSupport.generateRefreshToken(SAMPLE_MEMBER_ID);
        log.info("tokenFirst={}", secondToken);
        log.info("tokenSecond={}", secondToken);
        assertThat(firstToken).isNotEqualTo(secondToken);
        assertThat(this.jwtSupport.isValidRefreshToken(firstToken)).isFalse();
        assertThat(this.jwtSupport.isValidRefreshToken(secondToken)).isTrue();
    }
}
