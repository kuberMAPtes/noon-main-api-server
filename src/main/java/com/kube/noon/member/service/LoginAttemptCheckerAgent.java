package com.kube.noon.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * 로그인 성공,실패 시 호출되는 함수
 */
@Service
public class LoginAttemptCheckerAgent {

    private static final int MAX_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public LoginAttemptCheckerAgent(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void loginSucceeded(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        ops.getOperations().delete(key);

    }

    public void loginFailed(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Integer attempts = Integer.parseInt(ops.get(key) == null ? "0" : ops.get(key));
        attempts++;
        ops.set(key, attempts.toString());
    }

    public boolean isCaptchaRequired(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Integer attempts = Integer.parseInt(ops.get(key) == null ? "0" : ops.get(key));
        return attempts >= MAX_ATTEMPTS;
    }
}
