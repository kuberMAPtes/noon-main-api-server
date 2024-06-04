package com.kube.noon.chat.controller;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.dto.ChatEntranceDto;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.service.ChatroomService;
import com.kube.noon.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatroom")
public class ChatroomRestController {

    private final ChatroomService chatroomService;

    @Autowired
    public ChatroomRestController(ChatroomService chatroomService) {
        this.chatroomService = chatroomService;
    }

    // 채팅방 생성
    @PostMapping("addChatroom")
    public ChatroomDto addChatroom(@RequestBody ChatroomDto requestChatroom) throws Exception {
        System.out.println("        🐬[Controller] (requestdChatroom) => " + requestChatroom);

        // chatroom 테이블 칼럼생성
        ChatroomDto reponseChatroom = chatroomService.addChatroom(requestChatroom);
        System.out.println("        🐬[Controller] chatroomService.addChatroom return => " + reponseChatroom);
        List<ChatEntranceDto> responseChatEntranceList = chatroomService.getChatEntranceListByChatroom(reponseChatroom);
        System.out.println("        🐬[Controller] chatroomService.enterChatroom return => " + responseChatEntranceList );

        return reponseChatroom;
    }

    // 입장하려는 채팅방조회
    @GetMapping("/getChatroom")
    public Map<String, Object> getChatroom(@RequestParam("roomId") int roomId) throws Exception {
        System.out.println("Controller getChatroom(roomId) => " + roomId);

        // roomId 에 해당하는 채팅방 정보
        ChatroomDto searchedChatroom = chatroomService.getChatroomByRoomId(roomId);
        System.out.println("        🐬[Controller] 가져온 채팅방 정보 => " + searchedChatroom);

        // roomID 에 해당하는 채팅멤버 정보
        List<ChatEntranceDto> searchedChatEntranceList = chatroomService.getChatEntranceListByChatroom(searchedChatroom);
        System.out.println("        🐬[Controller] 가져온 채팅멤버 정보 => " + searchedChatEntranceList);

        Map<String, Object> result = new HashMap<>();
        result.put("ChatroomInfo", searchedChatroom);
        result.put("ChatEntrancesInfo", searchedChatEntranceList);
        System.out.println("        🐬[Controller] 날릴 Map 정보 => " + result);

        return result;
    }

    // 내 채팅방 목록 조회
    @GetMapping("getMyChatrooms")
    public List<ChatroomDto> getChatrooms(@RequestParam("userId") String userId) throws Exception {
        System.out.println("        🐬[Controller] (userId) => " + userId);
        return chatroomService.getChatroomListByMemberId(userId);
    }

}
