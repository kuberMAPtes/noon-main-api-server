package com.kube.noon.common.security.accesscontrol;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 리소스에 대한 접근을 특정 회원에게만 제한하고 싶을 때, AccessControl 어노테이션을
 * 붙인 클래스를 정의한다. AccessControl 어노테이션이 달린 클래스에 정의된 메소드들은
 * <code>AccessControlTrigger</code> 어노테이션이 붙어 있어야 한다.
 * AccessControl 어노테이션이 붙은 객체는 스프링 빈으로 등록된다.
 * @author PGD
 * @see com.kube.noon.setting.accesscontrol.SettingAccessControl
 * @see AccessControlTrigger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface AccessControl {
}
