package com.kube.noon.common.security.accesscontrol.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TestRequestPathTree {
    RequestPathTree<String> tree;

    @BeforeEach
    void beforeEach() {
        this.tree = new RequestPathTree<>();
    }

    @Test
    void test1() {
        this.tree.add("/", HttpMethod.GET, "element1");
        this.tree.add("/member", HttpMethod.GET, "element2");
        this.tree.add("/member/{memberId}", HttpMethod.GET, "element3");
        this.tree.add("/member/{memberId}/setting", HttpMethod.POST, "element4");

        String result1 = this.tree.getElement("/", HttpMethod.GET).orElseThrow(NullPointerException::new);
        String result2 = this.tree.getElement("/member", HttpMethod.GET).orElseThrow(NullPointerException::new);
        String result3 = this.tree.getElement("/member/{memberId}", HttpMethod.GET).orElseThrow(NullPointerException::new);
        String result4 = this.tree.getElement("/member/{memberId}/setting", HttpMethod.POST).orElseThrow(NullPointerException::new);

        assertThat(result1).isEqualTo("element1");
        assertThat(result2).isEqualTo("element2");
        assertThat(result3).isEqualTo("element3");
        assertThat(result4).isEqualTo("element4");
    }

    @Test
    void test2() {
        this.tree.add("/", HttpMethod.POST, "element1");
        this.tree.add("/member/", HttpMethod.GET, "element2");
        this.tree.add("/member/{memberId}", HttpMethod.GET, "element3");
        this.tree.add("/member/setting/{memberId}", HttpMethod.GET, "element4");
        this.tree.add("/member/setting/{memberId}/edit", HttpMethod.POST, "element5");
        this.tree.add("/feed", HttpMethod.GET, "element6");

        assertThat(extractElement("/", HttpMethod.POST)).isEqualTo("element1");
        assertThat(extractElement("/member", HttpMethod.GET)).isEqualTo("element2");
        assertThat(extractElement("/member/member_1", HttpMethod.GET)).isEqualTo("element3");
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> extractElement("/member/member_1", HttpMethod.POST));
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> extractElement("/member/setting", HttpMethod.GET));
        assertThat(extractElement("/member/setting/member_2", HttpMethod.GET)).isEqualTo("element4");
        assertThat(extractElement("/member/setting/member_5/edit", HttpMethod.POST)).isEqualTo("element5");
        assertThat(extractElement("/feed", HttpMethod.GET)).isEqualTo("element6");
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> extractElement("/feed", HttpMethod.POST));
    }

    @Test
    void test3() {
        this.tree.add("/feed", HttpMethod.GET, "element1");
        this.tree.add("/feed/{memberId}", HttpMethod.GET, "element2");
        this.tree.add("/feed/{memberId}/edit", HttpMethod.GET, "element3");
        this.tree.add("/member/feed", HttpMethod.GET, "element4");
        this.tree.add("/feed", HttpMethod.GET, "element5");

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> extractElement("/", HttpMethod.GET));
        assertThat(extractElement("/feed", HttpMethod.GET)).isEqualTo("element5");
        assertThat(extractElement("feed", HttpMethod.GET)).isEqualTo("element5");
        assertThat(extractElement("feed/member_1", HttpMethod.GET)).isEqualTo("element2");
        assertThat(extractElement("/feed/member_2/edit", HttpMethod.GET)).isEqualTo("element3");
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> extractElement("/feed/member_2/editt", HttpMethod.GET));
        assertThat(extractElement("member/feed/", HttpMethod.GET)).isEqualTo("element4");
        assertThat(extractElement("/feed", HttpMethod.GET)).isEqualTo("element5");
    }

    @Test
    void test4() {
        this.tree.add("/feed", HttpMethod.GET, "element1");
        this.tree.add("/feed/{memberId}", HttpMethod.GET, "element2");
        this.tree.add("/feed/{memberId}/edit", HttpMethod.GET, "element3");
        this.tree.add("/member/feed", HttpMethod.GET, "element4");
        this.tree.add("/feed", HttpMethod.GET, "element5");
        this.tree.add("/feed", HttpMethod.POST, "element6");

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> extractElement("/", HttpMethod.GET));
        assertThat(extractElement("/feed", HttpMethod.GET)).isEqualTo("element5");
        assertThat(extractElement("feed", HttpMethod.GET)).isEqualTo("element5");
        assertThat(extractElement("feed/member_1", HttpMethod.GET)).isEqualTo("element2");
        assertThat(extractElement("/feed/member_2/edit", HttpMethod.GET)).isEqualTo("element3");
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> extractElement("/feed/member_2/editt", HttpMethod.GET));
        assertThat(extractElement("member/feed/", HttpMethod.GET)).isEqualTo("element4");
        assertThat(extractElement("/feed", HttpMethod.GET)).isEqualTo("element5");
        assertThat(extractElement("/feed", HttpMethod.POST)).isEqualTo("element6");
    }

    private String extractElement(String path, HttpMethod httpMethod) {
        return this.tree.getElement(path, httpMethod).orElseThrow(NullPointerException::new);
    }
}