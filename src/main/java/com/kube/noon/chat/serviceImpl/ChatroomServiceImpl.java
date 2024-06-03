package com.kube.noon.chat.serviceImpl;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.domain.ChatroomMemberType;
import com.kube.noon.chat.domain.ChatroomType;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.repository.ChatEntranceRepository;
import com.kube.noon.chat.repository.ChatroomRepository;
import com.kube.noon.chat.service.ChatroomService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service("chatroomService")
@Transactional
public class ChatroomServiceImpl implements ChatroomService {

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private ChatEntranceRepository chatEntranceRepository;

    @Override
    public ChatroomDto addChatroom(ChatroomDto requestChatroom) {
        Chatroom chatroom = new Chatroom();
        chatroom.setChatroomName(requestChatroom.getChatroomName());
        chatroom.setChatroomCreatorId(requestChatroom.getChatroomCreatorId());
        chatroom.setChatroomMinTemp(requestChatroom.getChatroomMinTemp());

        // String 을 Enum으로 변환해서 Entity 삽입
        ChatroomType roomType = ChatroomType.valueOf(requestChatroom.getChatroomType());
        chatroom.setChatroomType(roomType);
        Chatroom savedChatroom = chatroomRepository.save(chatroom);

        // Entity를 Dto 로 변환해서 리턴
        return convertToDto(savedChatroom);
    }

    @Override
    public ChatEntrance enterChatroom(ChatroomDto requestChatroom) {
        System.out.println("ServiceImpl enterRoom requestChatroom => " + requestChatroom);

        Chatroom chatroom = chatroomRepository.findById((long) requestChatroom.getChatroomID()).orElse(null);
        if (chatroom != null) {
            ChatEntrance entrance = new ChatEntrance();
            entrance.setChatroomId(chatroom);
            entrance.setChatroomMemberId(requestChatroom.getChatroomCreatorId()); // 임시로 채팅방 생성자를 멤버로 추가
            entrance.setChatroomMemberType(ChatroomMemberType.MEMBER);
            entrance.setChatroomEnteredTime(LocalDateTime.now());
            entrance.setKicked(false);
            entrance.setActivated(true);

            return chatEntranceRepository.save(entrance);
        }
        return null;
    }

    @Override
    public List<ChatroomDto> getChatroomsByMemberId(String chatroomMemberId) {
        List<ChatEntrance> entrances = chatEntranceRepository.findByChatroomMemberId(chatroomMemberId);
        List<Chatroom> chatrooms = entrances.stream()
                .map(ChatEntrance::getChatroomId)
                .collect(Collectors.toList());
        return chatrooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChatroomDto getChatroomByRoomId(String roomId) {
        Chatroom chatroom = chatroomRepository.findById((long)Integer.parseInt(roomId)).orElse(null);
        if (chatroom != null) {
            return convertToDto(chatroom);
        }
        return null;
    }

    private ChatroomDto convertToDto(Chatroom chatroom) {
        ChatroomDto dto = new ChatroomDto();
        dto.setChatroomID(chatroom.getChatroomId());
        dto.setChatroomName(chatroom.getChatroomName());
        dto.setChatroomMinTemp(chatroom.getChatroomMinTemp());
        dto.setChatroomCreatorId(chatroom.getChatroomCreatorId());
        dto.setChatroomType(chatroom.getChatroomType().toString()); // Enum 값을 문자열로 변환하여 설정
        return dto;
    }
}
