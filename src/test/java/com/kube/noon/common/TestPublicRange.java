package com.kube.noon.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Java Enum 클래스를 사용하는 법을 예시 코드로 보여주는 곳입니다.
 */
@Slf4j
class TestPublicRange {

    @DisplayName("enum 사이의 == 연산")
    @Test
    void enumEquals() {
        log.info("PublicRange.PUBLIC == PublicRange.PUBLIC is {}", PublicRange.PUBLIC.equals(PublicRange.PUBLIC));
        log.info("PublicRange.PUBLIC == PublicRange.PUBLIC is {}", PublicRange.PUBLIC == PublicRange.PUBLIC);

        assertThat(PublicRange.PUBLIC.equals(PublicRange.PUBLIC)).isTrue();
        assertThat(PublicRange.PUBLIC == PublicRange.PUBLIC).isTrue();

        log.info("PublicRange.PUBLIC == PublicRange.PRIVATE is {}", PublicRange.PUBLIC.equals(PublicRange.PRIVATE));
        log.info("PublicRange.PUBLIC == PublicRange.PRIVATE is {}", PublicRange.PUBLIC == PublicRange.PRIVATE);

        assertThat(PublicRange.PUBLIC.equals(PublicRange.PRIVATE)).isFalse();
        assertThat(PublicRange.PUBLIC == PublicRange.PRIVATE).isFalse();
    }

    @DisplayName("enum의 name 메소드에 대해서")
    @Test
    void enum_name_method() {
        // name() 메소드를 호출하면 그 enum의 선언된 이름을 반환합니다.
        log.info("PublicRange.PUBLIC.name()={}", PublicRange.PUBLIC.name());

        assertThat(PublicRange.PUBLIC.name()).isEqualTo("PUBLIC");
    }

    @DisplayName("이름으로 enum 객체 찾기")
    @Test
    void findEnumByName() {
        // PublicRange.PUBLIC을 "PUBLIC"이라는 문자열로 찾기
        final String nameOfFinding = "PUBLIC";

        PublicRange publicRange = PublicRange.valueOf(nameOfFinding);

        log.info("publicRange={}", publicRange);

        assertThat(publicRange).isEqualTo(PublicRange.PUBLIC);
        assertThat(publicRange == PublicRange.PUBLIC).isTrue();
    }
}