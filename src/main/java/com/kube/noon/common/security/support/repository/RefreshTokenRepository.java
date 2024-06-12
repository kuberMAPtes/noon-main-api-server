package com.kube.noon.common.security.support.repository;

/**
 * Refresh Token을 저장하는 Repository를 정의한 인터페이스
 *
 * @author PGD
 * @see InMemoryRefreshTokenRepository
 * @see com.kube.noon.common.security.support.JwtSupport
 */
public interface RefreshTokenRepository {

    void save(String subject, String token);

    boolean exists(String subject, String token);

    void remove(String subject);
}
