package com.kube.noon.common.validator;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ValidationChain<T> {
    private final Map<Class<?>, ValidationChainRuleFunction<?>> validationChainMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <U extends T> void addRule(Class<U> classToValidate, ValidationChainRuleFunction<U> rule) {
        validationChainMap.put(classToValidate, rule);
    }

    @SuppressWarnings("unchecked")
    public <U extends T> void validate(U instance) {
        ValidationChainRuleFunction<U> validationChain = (ValidationChainRuleFunction<U>) validationChainMap.get(instance.getClass());
        if (validationChain != null) {
            validationChain.validate(instance);
        } else {
            throw new IllegalServiceCallException("No validation rules defined for " + instance.getClass().getName());
        }
    }
}
