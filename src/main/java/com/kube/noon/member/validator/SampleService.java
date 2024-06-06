package com.kube.noon.member.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SampleService {


    public void func1() {
        log.info("SampleService-func1");
    }

    public void func2() {
        log.info("SampleService-func2");
    }

    public void func3() {
        log.info("SampleService-func3");
    }

    public void func4() {
        log.info("SampleService-func4");
    }

    public void funcWithParameter(Integer param) {
        log.info("SampleService-funcWithParameter");
    }

    public void funcWithParameterNotProceed(Integer param) {
        log.info("SampleService-funcWithParameterNotProceed");
    }
}
