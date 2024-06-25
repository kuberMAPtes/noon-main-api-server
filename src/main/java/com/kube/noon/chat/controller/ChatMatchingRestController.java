package com.kube.noon.chat.controller;

import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.domain.ChatroomType;
import com.kube.noon.chat.dto.ChatApplyDto;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.service.ChatMatchingService;
import com.kube.noon.chat.service.ChatroomService;
import com.kube.noon.chat.serviceImpl.ChatMatchingServiceImpl;
import com.kube.noon.chat.serviceImpl.ChatroomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatMatching")
public class ChatMatchingRestController {

    private final ChatMatchingService chatMatchingService;
    private final ChatroomService chatroomService;

    @Autowired
    public ChatMatchingRestController(ChatMatchingService chatMatchingService, ChatroomService chatroomService) {
        this.chatMatchingService = chatMatchingService;
        this.chatroomService = chatroomService;
    }

    @GetMapping("/getChatApply")
    public ChatApplyDto getChatApply(int chatApplyId) throws Exception {
        System.out.println("        🐬[Controller getChatApply] 받은 ChatApplyId => " + chatApplyId);

        return chatMatchingService.getChatApply(chatApplyId);
    }

    @PostMapping("/applyChatting")
    public String applyChatting(@RequestBody ChatApplyDto chatApplyDto) throws Exception{
        // 송신자가 수신자에게 채팅 요청을 한다.
        System.out.println("        🐬[Controller applyChatting] 받은 applyChatting DTO => " + chatApplyDto);
        chatMatchingService.applyChatting(chatApplyDto);

        return "success";
    }

    @PostMapping("/acceptChatting")
    public ChatroomDto acceptChatting(@RequestBody ChatApplyDto chatApplyDto) throws Exception{
        // 채팅을 수락한다.
        System.out.println("        🐬[Controller acceptChatting] 받은 chatApply DTO => " + chatApplyDto);

        // accept 했다고 기록 남겨놓기
        chatApplyDto.setAccepted(true);
        chatMatchingService.acceptChatting(chatApplyDto);

        // 1:1 채팅방을 생성하고 입장도 시킨다.
        ChatroomDto privateRoomDto = new ChatroomDto();
        privateRoomDto.setChatroomCreatorId(chatApplyDto.getFromId());
        privateRoomDto.setChatroomType("PRIVATE_CHATTING");
        privateRoomDto.setChatroomName("사적채팅방 이름을 어떻게 하지 상대에 따라 유동적으로 가야될텐데");
        privateRoomDto.setChatroomMinTemp(0F);
        privateRoomDto.setInvitedMemberId(chatApplyDto.getToId());

        // 화면에서 받아서 채팅방으로 이동할 것
        return chatroomService.addChatroom(privateRoomDto);
    }

    @PostMapping("/rejectChatting")
    public ChatApplyDto rejectChatting(@RequestBody ChatApplyDto chatApplyDto) throws Exception{
        // 채팅을 거절한다.
        System.out.println("        🐬[Controller rejectChatting] 받은 chatReject DTO => " + chatApplyDto);

        return chatMatchingService.rejectChatting(chatApplyDto);
    }

    @GetMapping("/newChatApplyList")
    public List<ChatApplyDto> applyChatting(String memberId) throws Exception{
        // 새로운 채팅신청 목록을 불러온다.
        System.out.println("        🐬[Controller applyChatting] 받은 채팅신청목록 조회할 유저 => " + memberId);

        return chatMatchingService.newChatApplyList(memberId);
    }

}
