package com.kube.noon.common.validator;

/**
 * ValidationRule은
 * @param <T>
 */
@FunctionalInterface
public interface ValidationChainRuleFunction<T> {
    void validate(T t) throws IllegalServiceCallException;
}
