package com.kube.noon.common.validator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@SpringBootTest
class TestValidationAspect {

    @Autowired
    SampleService sampleService;

    @Test
    void func1() {
        this.sampleService.func1();
    }

    @Test
    void func2() {
        this.sampleService.func2();
    }

    @Test
    void func3() {
        assertThatExceptionOfType(IllegalCallerException.class).isThrownBy(this.sampleService::func3);
    }

    @Test
    void func4() {
        this.sampleService.func4();
    }

    @Test
    void funcWithParameter() {
        this.sampleService.funcWithParameter(13);
    }

    @Test
    void funcWithParameterNotProceed() {
        this.sampleService.funcWithParameterNotProceed(13);
    }
}