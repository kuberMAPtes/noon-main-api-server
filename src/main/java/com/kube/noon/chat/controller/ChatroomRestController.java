package com.kube.noon.chat.controller;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.dto.ChatEntranceDto;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.dto.ChatroomSearchResponseDto;
import com.kube.noon.chat.service.ChatroomSearchService;
import com.kube.noon.chat.service.ChatroomService;
import com.kube.noon.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatroom")
public class ChatroomRestController {

    private final ChatroomService chatroomService;
    private final ChatroomSearchService chatroomSearchService;

    @Autowired
    public ChatroomRestController(ChatroomService chatroomService, ChatroomSearchService chatroomSearchService) {
        this.chatroomService = chatroomService;
        this.chatroomSearchService = chatroomSearchService;
    }

    // 채팅방 생성
    @PostMapping("/addChatroom")
    public Map<String, Object> addChatroom(@RequestBody ChatroomDto requestChatroom) throws Exception {
        System.out.println("        🐬[Controller addChatroom] 받은 채팅방 DTO => " + requestChatroom);

        // chatroom 테이블 칼럼생성
        ChatroomDto reponseChatroom = chatroomService.addChatroom(requestChatroom);
        System.out.println("        🐬[Controller] 생성한 채팅방 정보 => " + reponseChatroom);
        List<ChatEntranceDto> responseChatEntranceList = chatroomService.getChatEntranceListByChatroom(reponseChatroom);
        System.out.println("        🐬[Controller] 생성한 채팅방 참여멤버 => " + responseChatEntranceList);

        Map<String, Object> result = new HashMap<>();
        result.put("ChatroomInfo", reponseChatroom);
        result.put("ChatEntrancesInfo", responseChatEntranceList);
        System.out.println("        🐬[Controller] 날릴 생성 Map 정보 => " + result);

        return result;
    }

    // 입장하려는 채팅방조회
    @GetMapping("/getChatroom")
    public Map<String, Object> getChatroom(@RequestParam("roomId") int roomId) throws Exception {
        System.out.println("        🐬[Controller getChatroom] 받은 채팅방 ID => " + roomId);

        // roomId 에 해당하는 채팅방 정보
        ChatroomDto searchedChatroom = chatroomService.getChatroomByRoomId(roomId);
        System.out.println("        🐬[Controller] 가져온 채팅방 정보 => " + searchedChatroom);

        // roomID 에 해당하는 채팅멤버 정보
        List<ChatEntranceDto> searchedChatEntranceList = chatroomService.getChatEntranceListByChatroom(searchedChatroom);
        System.out.println("        🐬[Controller] 가져온 채팅방 참여멤버 => " + searchedChatEntranceList.size() + "명 " + searchedChatEntranceList);

        Map<String, Object> result = new HashMap<>();
        result.put("ChatroomInfo", searchedChatroom);
        result.put("ChatEntrancesInfo", searchedChatEntranceList);
        System.out.println("        🐬[Controller] 날릴 가져온 Map 정보 => " + result);

        return result;
    }

    // 채팅방 입장하기
    @GetMapping("enterChatroom")
    public ChatEntranceDto enterChatroom(@RequestParam("roomId") int roomId, @RequestParam("memberId") String memberId) throws Exception {
        System.out.println("        🐬[Controller enterChatroom] 받은 멤버 ID, 채팅방 ID => " + roomId + " " + memberId);
        ChatEntranceDto entrance = chatroomService.enterChatroom(roomId, memberId);

        System.out.println("        🐬[Controller enterChatroom] 새로운 멤버를 입장시킨 입장멤버> " + entrance);
        return entrance;
    }

    // 내 채팅방 목록 조회
    @GetMapping("getMyChatrooms")
    public List<ChatroomDto> getChatrooms(@RequestParam("memberId") String memberId) throws Exception {
        System.out.println("        🐬[Controller] (memberId) => " + memberId);
        System.out.println("        🐬[Controller] getMyChatrooms return => " + chatroomSearchService.getChatroomListByMemberId(memberId));
        List<ChatroomDto> chatroomDtos = chatroomSearchService.getChatroomListByMemberId(memberId);

        // 채팅방 조회시 채팅방 참여멤버수도 함께조회
        if(!chatroomDtos.isEmpty()){
            for(ChatroomDto chatroomDto : chatroomDtos){
                List<ChatEntranceDto> chatEntrances = chatroomService.getChatEntranceListByChatroom(chatroomDto);

                chatroomDto.setChatroomEntrancesSize(chatEntrances.size());
            }
        }
        return chatroomDtos;
    }

    /**
     * 채팅방조회할때 넣은 ID가 어떤 값인지 확인하고 화면에 현재 로그인 된 유저 정보 띄우면서 리팩
     */

    @GetMapping("/searchChatroom")
    public ResponseEntity<Page<ChatroomSearchResponseDto>> searchChatroom(@RequestParam("searchKeyword") String searchKeyword,
                                                                          @RequestParam("page") int page) {
        return ResponseEntity.ok(this.chatroomSearchService.searchChatroomByChatroomName(searchKeyword, page));
    }

    @GetMapping("/kickChatroom")
    public Map<String, Object> kickChatroom(@RequestParam int chatroomId, @RequestParam String memberId) throws Exception {
        System.out.println("        🐬[Controller] 가져온 채팅방과 참여멤버 => " + chatroomId +" "+ memberId);

        // chatroomID, memberID 를 가지고 chatEntrance 를 조회해서 kicked 를 1로 바꿈

        return chatroomService.kickChatroom(chatroomId, memberId);
    }
}
