package com.kube.noon.common.validator;

/**
 * Validator가 RuleValidator를 구현하면 클래스별 검증 로직을 만들 수 있다.
 * 검증해야할 Dto와 Domain이 많을 때 유용할 수 있다.
 * @param <R>
 */
public interface RuleValidator<R> {
    void setRule(R repository);
}