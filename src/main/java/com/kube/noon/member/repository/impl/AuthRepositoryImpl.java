package com.kube.noon.member.repository.impl;

import com.kube.noon.member.repository.AuthRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class AuthRepositoryImpl implements AuthRepository {

    private final String PREFIX = "sms:";
    private final int LIMIT_TIME = 3 * 60;

    private final StringRedisTemplate stringRedisTemplate;

    public AuthRepositoryImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //Redis에 인증번호 저장
    //키는 휴대전화번호, 값은 인증번호
    public void createAuthentificationNumber(String phone, String certificationNumber) {
        stringRedisTemplate.opsForValue().set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    //휴대전화번호에 해당하는 인증번호 불러오기
    public String getAuthentificationNumber(String phone) {
        return stringRedisTemplate.opsForValue().get(PREFIX + phone);
    }

    //인증 완료 시, 인증번호 Redis에서 삭제
    public void deleteAuthentificationNumber(String phone) {
        stringRedisTemplate.delete(PREFIX + phone);
    }

    //Redis에 해당 휴대전화번호로 저장된 인증번호가 존재하는지 확인
    public boolean hasKey(String phone) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + phone));
    }


}
