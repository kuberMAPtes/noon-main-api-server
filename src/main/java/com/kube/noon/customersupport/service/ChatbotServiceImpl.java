package com.kube.noon.customersupport.service;

import com.kube.noon.customersupport.domain.ChatbotConversation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    private static final String UNIQUE_USER_ID = "awvoawhifhoizxihovihodsdasd";

    private final RestTemplate restTemplate;
    private final String chatbotUrl;

    public ChatbotServiceImpl(@Value("${chatbot.naver.url}") String chatbotUrl) {
        this.chatbotUrl = chatbotUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ChatbotConversation ask(String question) {
        JSONObject body = new JSONObject();
        body.put("userId", UNIQUE_USER_ID);
        body.put("timestamp", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        JSONArray content = new JSONArray();
        JSONObject contentElement = new JSONObject();
        contentElement.put("type", "text");
        JSONObject contentData = new JSONObject();
        contentData.put("details", question);
        contentElement.put("data", contentData);
        content.put(contentElement);
        body.put("content", content);
        body.put("event", "send");
        RequestEntity<String> requestEntity = null;
        try {
            requestEntity = RequestEntity.post(new URI(this.chatbotUrl))
                    .body(body.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        JSONObject responseBody = new JSONObject(this.restTemplate.exchange(requestEntity, String.class).getBody());
        String answer = responseBody.getJSONArray("content")
                .getJSONObject(0)
                .getJSONObject("data")
                .getString("details");

        ChatbotConversation conversation = new ChatbotConversation();
        conversation.setChatbotAnswer(answer);
        conversation.setUserQuestion(question);
        conversation.setAnswerTimeNDate(new Date());
        conversation.setQuestionTimeNDate(new Date());
        return conversation;
    }
}
