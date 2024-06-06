package com.kube.noon.member.validator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@SpringBootTest
class TestSValidationAspect {


    @Autowired
    SsampleServiceImpl ssampleServiceImpl;

    @Test
    void func1() {
        this.ssampleServiceImpl.func1();


    }

    @Test
    void func2() {
        this.ssampleServiceImpl.func2();
    }

    @Test
    void func3() {
        assertThatExceptionOfType(IllegalCallerException.class).isThrownBy(this.ssampleServiceImpl::func3);
    }

    @Test
    void func4() {
        this.ssampleServiceImpl.func4();
    }

    @Test
    void funcWithParameter() {
        this.ssampleServiceImpl.funcWithParameter(13);
    }

    @Test
    void funcWithParameterNotProceed() {
        this.ssampleServiceImpl.funcWithParameterNotProceed(13);
    }
}