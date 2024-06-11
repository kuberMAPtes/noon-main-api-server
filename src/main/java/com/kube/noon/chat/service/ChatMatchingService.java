package com.kube.noon.chat.service;

import com.kube.noon.chat.domain.ChatApply;
import com.kube.noon.chat.dto.ChatApplyDto;

import java.util.List;

public interface ChatMatchingService {

    public String applyChatting(ChatApplyDto chatApplyDto) throws Exception;

    public ChatApplyDto getChatApply(int chatApplyId) throws Exception;

    public ChatApplyDto acceptChatting(ChatApplyDto chatApplyDto) throws Exception;

    public ChatApplyDto rejectChatting(ChatApplyDto chatApplyDto) throws Exception;

    public List<ChatApplyDto> newChatApplyList(String memberId) throws Exception;
}