package com.kube.noon.common.validator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validator(targetClass = SampleService.class)
public class SampleValidator {

    public boolean func1() {
        log.info("Validator-func1");
        return true;
    }

    public boolean func2() {
        log.info("Validator-func2");
        return false;
    }

    public boolean func3() {
        log.info("Validator-func3");
        throw new IllegalCallerException();
    }

    public boolean func33() {
        log.info("Not called");
        return true;
    }

    public boolean funcWithParameter(Integer param) {
        log.info("Validator-funcWithParameter");
        return true;
    }

    public boolean funcWithParameterNotProceed(Integer param) {
        log.info("Validator-funcWithParameterNotProceed");
        return param < 13;
    }
}
