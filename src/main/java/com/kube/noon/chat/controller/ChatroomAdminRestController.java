package com.kube.noon.chat.controller;

import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.service.ChatroomSearchService;
import com.kube.noon.chat.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adminChatroom")
public class ChatroomAdminRestController {

    private final ChatroomSearchService chatroomSearchService;
    private final ChatroomService chatroomService;

    @Autowired
    public ChatroomAdminRestController(ChatroomSearchService chatroomSearchService, ChatroomService chatroomService) {
        this.chatroomSearchService = chatroomSearchService;
        this.chatroomService = chatroomService;
    }

    /**
     * 채팅방 검색
     * @param searchKeywordChatroomName
     * @return
     * @throws Exception
     */
    @GetMapping("chatroomSearch")
    public List<ChatroomDto> chatroomSearch(String searchKeywordChatroomName) throws Exception {
        List<ChatroomDto> chatrooms = chatroomSearchService.getChatroomListByChatroomName(searchKeywordChatroomName);

        return chatrooms;
    }

    /**
     * 채팅방 삭제
     * @param chatroomId
     * @return
     * @throws Exception
     */
    @GetMapping("chatroomDelete")
    public String chatroomDelete(int chatroomId) throws Exception {

        return chatroomService.deleteChatroom(chatroomId);
    }

}
