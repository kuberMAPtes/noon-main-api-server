package com.kube.noon.common.validator;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 검증 로직에서 각 문제가 있는 부분을 저장하는 객체
 * IllegalServiceCallException에 저장된다.
 * Key에는 문제가 있는 Parameter의 이름이 담길 수 있고
 * Value에는 아무 객체가 올 수 있으나 보통 String 객체 (검증 실패 메시지)가 담긴다.
 *
 * @author PGD
 * @see IllegalServiceCallException
 */
@NoArgsConstructor
public class Problems extends HashMap<String, Object> {

    public Problems(Map<String, Object> problems) {
        super(problems);
    }
}
