package com.kube.noon.common.security.accesscontrol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>{@link AccessControl} 어노테이션이 붙은 클래스에 정의된 메소드에 붙는 어노테이션이다.</p>
 * - 각 메소드의 return type은 boolean이어야 한다.<br>
 * - 각 메소드가 true를 반환하면 그대로 진행되고, false를 반환하면 403 Forbidden 에러가 전송된다.<br>
 * - 각 메소드의 첫 번째 파라미터는 {@link com.kube.noon.member.domain.Member} 타입이어야 하고,
 * 두 번째 파라미터는 {@link reactor.netty.http.server.HttpServerRequest}여야 한다.<br>
 * - 각 메소드의 이름은 자유롭게 지정할 수 있다.<br>
 * - 어노테이션의 <code>path</code>는 접근을 제한할 URI를 지정해야 한다.<br>
 * - 어노테이션의 <code>method</code>는 접근을 제한할 HTTP Method를 지정해야 한다. <code>method</code>의
 * 값은 "GET", "POST", "DELETE", "PATCH", "PUT", "HEAD"와 같은 값을 가져야 하며, 오타가 들어가선 안 된다.
 * @author PGD
 * @see AccessControl
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessControlTrigger {

    public String path();

    public String method();
}
