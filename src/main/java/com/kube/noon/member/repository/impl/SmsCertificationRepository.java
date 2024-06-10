package com.kube.noon.member.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class SmsCertificationRepository {

    private final String PREFIX = "sms:";
    private final int LIMIT_TIME = 3 * 60;

    private final StringRedisTemplate stringRedisTemplate;

    //Redis에 인증번호 저장
    //키는 휴대전화번호, 값은 인증번호
    public void createSmsCertification(String phone, String certificationNumber) {
        stringRedisTemplate.opsForValue().set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    //휴대전화번호에 해당하는 인증번호 불러오기
    public String getSmsCertification(String phone) {
        return stringRedisTemplate.opsForValue().get(PREFIX + phone);
    }

    //인증 완료 시, 인증번호 Redis에서 삭제
    public void deleteSmsCertification(String phone) {
        stringRedisTemplate.delete(PREFIX + phone);
    }

    //Redis에 해당 휴대전화번호로 저장된 인증번호가 존재하는지 확인
    public boolean hasKey(String phone) {
        return stringRedisTemplate.hasKey(PREFIX + phone);
    }

}
