package com.kube.noon.common.security.support.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory 기반 Refresh Token 저장소. 발급된 Refresh Token을 저장한다.
 *
 * @author PGD
 * @see com.kube.noon.common.security.support.JwtSupport
 */
@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {
    private static final Map<String, String> store = new ConcurrentHashMap<>();

    @Override
    public void save(String subject, String token) {
        store.put(subject, token);
    }

    @Override
    public boolean exists(String subject, String token) {
        String find = store.get(subject);
        return find != null && find.equals(token);
    }

    @Override
    public void remove(String subject) {
        store.remove(subject);
    }
}
