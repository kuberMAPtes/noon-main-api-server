package com.kube.noon.common.validator;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>검증을 담당하는 클래스에 이 어노테이션을 붙이면 된다.</p>
 * <p>이 어노테이션이 붙는 검증 클래스에 검증 대상 클래스의 메소드 이름과 Parameter가 동일해야 한다.</p>
 * <p>사용법은 검증 메소드의 리턴 타입에 따라 세 가지로 나뉜다.</p><br>
 * 1. 리턴 타입이 void일 경우<br>
 * <p>문제가 있으면 IllegalServiceCallException을 던진다.</p><br>
 * 2. 리턴 타입이 boolean일 경우<br>
 * <p>true를 반환하면 그대로 진행되고, false를 반환하면 더이상 진행되지 않는다.<br>
 * 이 경우에도 IllegalServiceCallException을 던짐으로써 대상 서비스 메소드를 실행시키지 않을 수 있다.<br></p><br>
 * 3. 리턴 타입이 Problems 객체일 경우<br>
 * <p>어떤 파라미터에 어떤 문제가 있는지 Problems 객체에 담아서 이를 반환할 수 있다.<br>
 * Problems 객체에 요소가 들어 있을 경우 (i.e., Problems.size() > 0)<br>
 * 대상 서비스 메소드가 진행되지 않는다.<br>
 * Problems 객체는 IllegalServiceCallException의 생성자 argument로도 전달할 수 있다.<br></p>
 *
 * @author PGD
 * @see ValidationAspect
 * @see Problems
 * @see IllegalServiceCallException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Validator {

    public Class<?> targetClass();
}
