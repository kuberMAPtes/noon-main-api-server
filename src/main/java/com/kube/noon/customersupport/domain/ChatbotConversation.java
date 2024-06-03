package com.kube.noon.customersupport.domain;

import lombok.Data;
import java.util.Date;

@Data
public class ChatbotConversation {
    private String userQuestion;
    private String chatbotAnswer;
    private Date questionTimeNDate;
    private Date answerTimeNDate;
}
