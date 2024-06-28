package com.kube.noon.member.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 로그인 성공,실패 시 호출되는 함수
 */
@Component
@Slf4j
public class LoginAttemptCheckerAgent {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_TIME = 30;//TTL기능 이용 time to live

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
        // 꺼내서 null이면 0이고, 있으면 키에 대한 밸류를 가져옴 밸류가 횟수임.
        Integer attempts = Integer.parseInt(ops.get(key) == null ? "0" : ops.get(key));
        attempts++;
        ops.set(key, attempts.toString());

        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.expire(key, LOCK_TIME, TimeUnit.SECONDS);
        }
    }

    public boolean isLoginLocked(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key)) && redisTemplate.getExpire(key) > 0;
    }

}
