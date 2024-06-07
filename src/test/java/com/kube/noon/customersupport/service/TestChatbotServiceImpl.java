package com.kube.noon.customersupport.service;

import com.kube.noon.customersupport.domain.ChatbotConversation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class TestChatbotServiceImpl {

    @Autowired
    ChatbotServiceImpl chatbotService;

    @Test
    void test() {
        ChatbotConversation ask = chatbotService.ask("NOON이 뭐야?");
        log.info("ask={}", ask);
    }
}