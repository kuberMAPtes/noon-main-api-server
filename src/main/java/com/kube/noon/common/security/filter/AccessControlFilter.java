package com.kube.noon.common.security.filter;

import com.kube.noon.common.security.accesscontrol.AccessControl;
import com.kube.noon.common.security.accesscontrol.AccessControlTrigger;
import com.kube.noon.common.security.accesscontrol.util.RequestPathTree;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.support.BearerTokenSupport;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.kube.noon.common.security.SecurityConstants.*;

@Slf4j
public class AccessControlFilter extends OncePerRequestFilter {
    private final List<BearerTokenSupport> tokenSupportSet;
    private final ApplicationContext context;
    private final RequestPathTree<BeanAndMethod> accessControls;
    private final MemberRepository memberRepository;

    public AccessControlFilter(List<BearerTokenSupport> tokenSupportSet,
                               ApplicationContext context,
                               MemberRepository memberRepository) {
        this.tokenSupportSet = tokenSupportSet;
        this.context = context;
        this.memberRepository = memberRepository;
        this.accessControls = new RequestPathTree<>();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.context.getBeansWithAnnotation(AccessControl.class)
                .values()
                .forEach((bean) -> {
                    Method[] methods = AopProxyUtils.ultimateTargetClass(bean).getMethods();
                    for (Method method : methods) {
                        AccessControlTrigger trigger = method.getAnnotation(AccessControlTrigger.class);
                        if (trigger == null) {
                            log.warn("AccessControlTrigger annotation is missing for {}.{}",
                                    bean.getClass(),
                                    method.getName());
                            continue;
                        }
                        String httpMethod = trigger.method().trim().toUpperCase();
                        if (!isMethodNameAcceptable(httpMethod)) {
                            log.warn("HttpMethod is not acceptable: {}", httpMethod);
                            return;
                        }
                        String path = trigger.path().trim();
                        log.trace("path={}, method={}", path, httpMethod);
                        this.accessControls.add(path, HttpMethod.valueOf(httpMethod), new BeanAndMethod(bean, method));
                    }
                });
        log.trace("{}", this.accessControls);
    }

    private boolean isMethodNameAcceptable(String methodName) {
        return Set.of("GET", "POST", "DELETE", "PATCH", "PUT", "HEAD")
                .contains(methodName);
    }

    @AllArgsConstructor
    @Getter
    @ToString
    private static class BeanAndMethod {
        private Object bean;
        private Method method;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod().toUpperCase());
        log.trace("{} {}", httpMethod, request.getRequestURI());
        String path = request.getRequestURI();

        BeanAndMethod beanAndMethod = this.accessControls.getElement(path, httpMethod).orElse(null);

        if (beanAndMethod == null) {
            log.trace("No access control for {} {}", httpMethod, path);
            log.trace("Just proceed");
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> cookies = getCookiesOnDemand(request);
        String accessToken = cookies.get(ACCESS_TOKEN_COOKIE_KEY.get());
        String tokenTypeStr = cookies.get(TOKEN_TYPE_COOKIE_KEY.get());

        Member member;
        try {
            member = extractMember(accessToken, TokenType.valueOf(tokenTypeStr));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        Object accessControlBean = beanAndMethod.getBean();
        Method accessControlMethod = beanAndMethod.getMethod();
        try {
            Class<?>[] parameterTypes = accessControlMethod.getParameterTypes();
            if (parameterTypes.length != 2
                    || parameterTypes[0] != Member.class
                    || parameterTypes[1] != HttpServletRequest.class) {
                throw new RuntimeException("Parameter types of access control is wrong: "
                        + accessControlBean.getClass() + "." + accessControlMethod.getName());
            }
            Boolean result = (Boolean)accessControlMethod.invoke(accessControlBean, member, request);
            if (result) {
                log.trace("Access Control: Request for {} {} is allowed", httpMethod, path);
                filterChain.doFilter(request, response);
                return;
            }
            log.warn("Access Control: Request for {} {} is not allowed", httpMethod, path);
        } catch (ClassCastException e) {
            throw new RuntimeException(
                    String.format(
                            "Access control method %s.%s doesn't return a boolean",
                            accessControlBean.getClass(),
                            accessControlMethod.getName()
                    ),
                    e
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.warn("An exception has occured for {} {}", httpMethod, path, e.getTargetException());
            log.warn("Access control method={}.{}", accessControlBean.getClass(), accessControlMethod.getName());
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }

    private String getKey(String method, String path) {
        return method.trim().toLowerCase() + "_" + path.trim().toLowerCase();
    }

    private Map<String, String> getCookiesOnDemand(HttpServletRequest request) {
        Map<String, String> cookies = new HashMap<>();
        Arrays.stream(request.getCookies())
                .filter((c) -> c.getName().equals(TOKEN_TYPE_COOKIE_KEY.get()) || c.getName().equals(ACCESS_TOKEN_COOKIE_KEY.get()))
                .forEach((c) -> cookies.put(c.getName(), c.getValue()));
        return cookies;
    }

    private Member extractMember(String accessToken, TokenType tokenType) {
        for (BearerTokenSupport ts : this.tokenSupportSet) {
            if (ts.supports(tokenType)) {
                String memberId = ts.extractMemberId(accessToken);
                Optional<Member> member = this.memberRepository.findMemberById(memberId);
                return member.orElseThrow(NoSuchElementException::new);
            }
        }
        throw new IllegalArgumentException();
    }
}
