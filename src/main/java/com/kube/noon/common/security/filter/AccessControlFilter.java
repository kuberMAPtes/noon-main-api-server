package com.kube.noon.common.security.filter;

import com.kube.noon.common.security.accesscontrol.AccessControl;
import com.kube.noon.common.security.accesscontrol.Trigger;
import com.kube.noon.common.security.authentication.authtoken.TokenType;
import com.kube.noon.common.security.support.BearerTokenSupport;
import com.kube.noon.member.domain.Member;
import com.kube.noon.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.kube.noon.common.security.SecurityConstants.*;

@Slf4j
public class AccessControlFilter extends OncePerRequestFilter {
    private final List<BearerTokenSupport> tokenSupportSet;
    private final ApplicationContext context;
    private final Map<String, AccessControl> accessControls;
    private final MemberRepository memberRepository;

    public AccessControlFilter(List<BearerTokenSupport> tokenSupportSet,
                               ApplicationContext context,
                               MemberRepository memberRepository) {
        this.tokenSupportSet = tokenSupportSet;
        this.context = context;
        this.accessControls = new HashMap<>();
        this.memberRepository = memberRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.context.getBeansOfType(AccessControl.class)
                .values()
                .forEach((bean) -> {
                    Trigger annotation = AopProxyUtils.ultimateTargetClass(bean).getAnnotation(Trigger.class);
                    if (annotation == null) {
                        log.error("{} has no Trigger annotation", bean.getClass());
                        throw new RuntimeException();
                    }
                    String method = annotation.method().trim().toLowerCase();
                    String path = annotation.path().trim().toLowerCase();
                    String key = method + "_" + path;
                    this.accessControls.put(key, bean);
                });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod().trim().toLowerCase();
        String path = request.getRequestURI().trim().toLowerCase();

        AccessControl accessControl = this.accessControls.get(method + "_" + path);

        if (accessControl == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> cookies = getCookiesOnDemand(request);
        String accessToken = cookies.get(ACCESS_TOKEN_COOKIE_KEY.get());
        String tokenTypeStr = cookies.get(TOKEN_TYPE_COOKIE_KEY.get());

        Member member;
        try {
            member = extractMember(accessToken, TokenType.valueOf(tokenTypeStr));
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        if (accessControl.isAllowed(member, request)) {
            filterChain.doFilter(request, response);
        } else {
            throw new AccessDeniedException(member.getMemberId() + " is not allowed for " + method.toUpperCase() + " " + path);
        }
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
