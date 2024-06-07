package com.kube.noon.common.validator;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ValidationChain {
    private final Map<Class<?>, ValidationChainRuleFunction<?>> validationChainMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void addRule(Class<T> classToValidate, ValidationChainRuleFunction<T> rule) {
        validationChainMap.put(classToValidate, rule);
    }

    @SuppressWarnings("unchecked")
    public <T> void validate(T instance) {
        ValidationChainRuleFunction<T> validationChain = (ValidationChainRuleFunction<T>) validationChainMap.get(instance.getClass());
        if (validationChain != null) {
            validationChain.validate(instance);
        } else {
            throw new IllegalServiceCallException("No validation rules defined for " + instance.getClass().getName());
        }
    }
}
