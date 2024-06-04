package com.kube.noon.notification.service.sender;

import com.kube.noon.member.domain.Member;
import com.kube.noon.notification.domain.NotificationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class TestCoolSmsNotificationAgent {
    static String receiverPhoneNumber = "input your phone number";
    static Properties properties = new Properties();

    CoolSmsNotificationAgent notificationAgent;

    @BeforeAll
    static void beforeAll() throws IOException {
        try (InputStream inputStream = TestCoolSmsNotificationAgent.class
                .getResourceAsStream("/application-key.properties")) {
            properties.load(inputStream);
        }
    }

    @BeforeEach
    void beforeEach() {
        this.notificationAgent = new CoolSmsNotificationAgent(
                properties.getProperty("cool-sms.accessk-key"),
                properties.getProperty("cool-sms.secret-key"),
                properties.getProperty("cool-sms.from-phone-number")
        );
    }

    @DisplayName("직접 문자 전송")
    @Test
    void test() {
        Member member = new Member();
        member.setPhoneNumber(receiverPhoneNumber);
        this.notificationAgent.send(member, "아 이거 왜 안 돼", NotificationType.REPORT);
    }
}