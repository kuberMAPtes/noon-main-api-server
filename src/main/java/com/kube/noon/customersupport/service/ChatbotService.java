package com.kube.noon.customersupport.service;

import com.kube.noon.customersupport.domain.ChatbotConversation;

public interface ChatbotService {

    public ChatbotConversation ask(String question);
}
