package com.kube.noon.common.security.support.repository;

public interface RefreshTokenRepository {

    void save(String subject, String token);

    boolean exists(String subject, String token);

    void remove(String subject);
}
