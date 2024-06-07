package com.kube.noon.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

import static com.kube.noon.common.logging.ConsoleColorCode.*;

@Slf4j
@Aspect
public class TraceLoggingAspect {

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceAnnotation() {
    }

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repositoryAnnotation() {
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerAnnotation() {
    }

    @Pointcut("execution(public * *(..))")
    public void onlyPublicMethods() {
    }

    @Around("serviceAnnotation() && onlyPublicMethods()")
    public Object logServices(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint, CYAN);
    }

    @Around("repositoryAnnotation() && onlyPublicMethods()")
    public Object logRepositories(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint, YELLOW);
    }

    @Around("controllerAnnotation() && onlyPublicMethods()")
    public Object logControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint, GREEN);
    }

    private Object log(ProceedingJoinPoint joinPoint, ConsoleColorCode colorCode) throws Throwable {
        System.out.print(colorCode.get());
        log.trace("[JoinPoint] {}", joinPoint.getSignature());
        log.trace("[Params] {}{}", Arrays.toString(joinPoint.getArgs()), RESET.get());
        Object returned = joinPoint.proceed();
        System.out.print(colorCode.get());
        if (returned != null) {
            log.trace("[Return] {}", returned);
        }
        log.trace("[Return Type] {}{}", returned == null ? "void" : returned.getClass(), RESET.get());
        return returned;
    }
}
