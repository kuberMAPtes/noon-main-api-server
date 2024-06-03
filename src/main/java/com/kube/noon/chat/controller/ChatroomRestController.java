package com.kube.noon.chat.controller;

import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.service.ChatroomService;
import com.kube.noon.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatroom")
public class ChatroomRestController {

    private final ChatroomService chatroomService;

    @Autowired
    public ChatroomRestController(ChatroomService chatroomService) {
        this.chatroomService = chatroomService;
    }

    @PostMapping("addChatroom")
    public ChatroomDto addChatroom(@RequestBody ChatroomDto requestChatroom) throws Exception {
        System.out.println("Controller requestdChatroom => " + requestChatroom);

        ChatroomDto reponseChatroom = chatroomService.addChatroom(requestChatroom);
        System.out.println("Controller addChatroom => " + reponseChatroom);

        System.out.println("Controller enterChatroom =>" + chatroomService.enterChatroom(reponseChatroom));

        return reponseChatroom;
    }

    // 입장하려는 채팅방조회
    @GetMapping("/getChatroom")
    public ChatroomDto getChatroom(@RequestParam("roomId") String roomId) throws Exception {
        System.out.println("Controller getChatroom(roomId)=> " + roomId);

        ChatroomDto searchedChatroom = chatroomService.getChatroomByRoomId(roomId);
        System.out.println("가져온 채팅방 정보=> " + searchedChatroom);
        return searchedChatroom;
    }

    // 내 채팅방 목록 전체조회
    @GetMapping("getMyChatrooms")
    public List<ChatroomDto> getChatrooms(@RequestParam("userId") String userId) throws Exception {
        System.out.println("Controller => " + userId);
        return chatroomService.getChatroomsByMemberId(userId);
    }

}
