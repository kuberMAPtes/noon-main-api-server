package com.kube.noon.common.validator;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 검증을 담당하는 클래스에 이 어노테이션을 붙이면 된다.
 * 이 어노테이션이 붙는 검증 클래스에 검증 대상 클래스의 메소드 이름과 Parameter가 동일해야 한다.
 * 이 어노테이션이 붙는 검증 클래스의 메소드들은 boolean 타입을 반환함으로써 검증 대상 메소드를 실행시킬지
 * 실행시키지 않을지 결정할 수 있다.
 * 그외에 IllegalServiceCallException을 선언할 수도 있다.
 *
 * @author PGD
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Validator {

    public Class<?> targetClass();
}
