package com.kube.noon.common.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 검증을 위한 Aspect
 *
 * @author PGD
 */
@Slf4j
@Aspect
public class ValidationAspect {
    private final ApplicationContext context;
    private final Map<Class<?>, BeanAndMethod> beanMap;

    public ValidationAspect(ApplicationContext context) {
        this.context = context;
        this.beanMap = new HashMap<>();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.context.getBeansWithAnnotation(Validator.class)
                .values()
                .forEach((bean) -> {
                    Class<?> cls = bean.getClass().getAnnotation(Validator.class).targetClass();
                    Method[] publicMethods = bean.getClass().getMethods();
                    Map<String, Method> methodMap = new HashMap<>();
                    for (Method method : publicMethods) {
                        methodMap.put(method.getName(), method);
                    }
                    this.beanMap.put(cls, new BeanAndMethod(bean, methodMap));
                });
    }

    @AllArgsConstructor
    @Getter
    @ToString
    private static class BeanAndMethod {
        private Object bean;
        private Map<String, Method> methodMap;
    }

    @Around("@within(org.springframework.stereotype.Service)")
    public Object validateService(ProceedingJoinPoint jp) throws Throwable {
        Class<?> targetClass = jp.getTarget().getClass();
        BeanAndMethod beanAndMethod = this.beanMap.get(targetClass);
        if (beanAndMethod == null) {
            return jp.proceed(jp.getArgs());
        }

        String targetMethodName = jp.getSignature().getName();
        Method validationMethod = beanAndMethod.getMethodMap().get(targetMethodName);

        if (validationMethod == null) {
            return jp.proceed(jp.getArgs());
        }

        try {
            Object returnValue = jp.getArgs().length == 0
                    ? validationMethod.invoke(beanAndMethod.getBean())
                    : validationMethod.invoke(beanAndMethod.getBean(), jp.getArgs());
            if (returnValue == null) {
                return jp.proceed(jp.getArgs());
            }
            Boolean availableToProceed = (Boolean)returnValue;
            if (availableToProceed) {
                return jp.proceed(jp.getArgs());
            } else {
                return null; // TODO: What to do?
            }
        } catch (ClassCastException e) {
            return jp.proceed(jp.getArgs());
        } catch (IllegalArgumentException e) {
            log.warn("검증 메소드와 타겟 메소드의 파라미터가 일치하지 않습니다.", e);
            return jp.proceed(jp.getArgs());
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
