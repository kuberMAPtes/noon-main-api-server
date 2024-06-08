package com.kube.noon.chat.serviceImpl;

import com.kube.noon.chat.domain.ChatEntrance;
import com.kube.noon.chat.domain.Chatroom;
import com.kube.noon.chat.domain.ChatroomMemberType;
import com.kube.noon.chat.domain.ChatroomType;
import com.kube.noon.chat.dto.ChatEntranceDto;
import com.kube.noon.chat.dto.ChatroomDto;
import com.kube.noon.chat.repository.ChatEntranceRepository;
import com.kube.noon.chat.repository.ChatroomRepository;
import com.kube.noon.chat.service.ChatroomService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("chatroomService")
@Transactional
public class ChatroomServiceImpl implements ChatroomService {

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private ChatEntranceRepository chatEntranceRepository;

    // 채팅방 생성
    @Override
    public ChatroomDto addChatroom(ChatroomDto requestChatroom) {
        // 명령받은 채팅방정보 DTO를 entity로 만듦
        Chatroom chatroom = new Chatroom();
        chatroom.setChatroomName(requestChatroom.getChatroomName());
        chatroom.setChatroomCreatorId(requestChatroom.getChatroomCreatorId());
        chatroom.setChatroomMinTemp(requestChatroom.getChatroomMinTemp());
        ChatroomType roomType = ChatroomType.valueOf(requestChatroom.getChatroomType()); // String 을 Enum으로 변환해서 Entity 삽입
        chatroom.setChatroomType(roomType);
        Chatroom savedChatroom = chatroomRepository.save(chatroom);

        // 채팅생성자가 채팅참여멤버 에 안들어갓누
        ChatEntrance chatEntrance = new ChatEntrance();
        chatEntrance.setChatroom(savedChatroom);
        chatEntrance.setChatroomMemberId(requestChatroom.getChatroomCreatorId());
        chatEntrance.setChatroomMemberType(ChatroomMemberType.OWNER);
        ChatEntrance savedChatEntrance = chatEntranceRepository.save(chatEntrance);

        System.out.println("        🦐[ServiceImpl] 생성자를 채팅멤버에 저장 => " + savedChatEntrance);

        // Entity를 Dto 로 변환해서 리턴
        return convertToChatroomDto(savedChatroom);
    }

    // 채팅방 참여멤버를 채팅방으로 조회 (테스트위해 방 번호 고정해놓음)
    @Override
    public List<ChatEntranceDto> getChatEntranceListByChatroom(ChatroomDto requestChatroom) {
        System.out.println("        🦐[ServiceImpl] getChatEntranceListByChatroom requestChatroom => " + requestChatroom);

        // DB에서 찾아온 채팅방
        Chatroom chatroom = chatroomRepository.findById(requestChatroom.getChatroomID()).orElse(null);
        System.out.println("        🦐[ServiceImpl] chatroom Repository 로 찾은 채팅방 결과 => " + chatroom);

        // 채팅방에 참여한 사람 정보 얻어야함
        List<ChatEntrance> entrances = chatEntranceRepository.findChatEntrancesByChatroom(chatroom);
        System.out.println("        🦐[ServiceImpl] chatEntrance Repository 로 찾은 채팅멤버 결과 "+ entrances.size() + entrances );

        List<ChatEntranceDto> entranceDtos = new ArrayList<>();

        if (!entrances.isEmpty()) {

            for (ChatEntrance entrance : entrances) {
                ChatEntranceDto entranceDto = new ChatEntranceDto();

                entranceDto.setChatroomId(entrance.getChatroom().getChatroomId());
                entranceDto.setChatroomMemberId(entrance.getChatroomMemberId());
                entranceDto.setChatroomMemberType(entrance.getChatroomMemberType());
                entranceDto.setChatroomEnteredTime(entrance.getChatroomEnteredTime());
                entranceDto.setKicked(entrance.isKicked());

                entranceDtos.add(entranceDto);
            }
        }

        return entranceDtos;
    }

/////////////////////////////////////// 고급 JPA 코드사용 공부 필요////////////////////////////
    // 채팅방 참여멤버를 채팅방ID로 조회
    // input : chatroomId (Jpa가 join 해서 Chatroom 타입이어야함)
    // output : List<chatEntrance>
    @Override
    public List<ChatEntranceDto> getChatEntranceListbyRoomId(int chatroomID) throws Exception {

        // JPA chatEntrance Entity 의 chatroomId 가 Chatroom 과 연관관계여서 ChatroomEntity를 만들어주고 있음
        Chatroom temp = new Chatroom();
        temp.setChatroomId(chatroomID);

        // 채팅방과 엮여있는 멤버들 전부조회
        List<ChatEntrance> entrances = chatEntranceRepository.findChatEntrancesByChatroom(temp);
        List<ChatEntranceDto> result = entrances.stream()
                .map(this::convertToChatEntranceDto)
                .collect(Collectors.toList());
        System.out.println("result"+result);

        return result;
    }

    // 채팅방 목록을 회원ID로 조회
    @Override
    public List<ChatroomDto> getChatroomListByMemberId(String chatroomMemberId) {

        // ChatEntrance 목록을 조회하여 memberID가 입장한 채팅방을 체크
        List<ChatEntrance> entrances = chatEntranceRepository.getChatEntrancesByChatroomMemberId(chatroomMemberId);

        // 입장한 채팅방을 기준으로 채팅방에 대한 상세한 정보 Chatroom list를 얻음
        List<Chatroom> chatrooms = entrances.stream()
                .map(ChatEntrance::getChatroom)
                .collect(Collectors.toList());

        // ChatroomList 를 Dto 로 변환후 return
        return chatrooms.stream()
                .map(this::convertToChatroomDto)
                .collect(Collectors.toList());
    }
/////////////////////////////////////////////////////////////////////////////////////////

    // 채팅방에 대한 정보를 채팅방ID로 조회
    @Override
    public ChatroomDto getChatroomByRoomId(int chatroomID) {
        System.out.println("        🦐[ServiceImpl] getChatroomByRoomId (chatroomId) => " + chatroomID);

        Chatroom chatroom = chatroomRepository.findById(chatroomID).orElse(null);
        if (chatroom != null) {
            System.out.println("        🦐[ServiceImpl] getChatroomByRoomId return => " + convertToChatroomDto(chatroom));

            return convertToChatroomDto(chatroom);
        }
        return null;
    }



    private ChatroomDto convertToChatroomDto(Chatroom chatroom) {
        ChatroomDto dto = new ChatroomDto();
        dto.setChatroomID(chatroom.getChatroomId());
        dto.setChatroomName(chatroom.getChatroomName());
        dto.setChatroomMinTemp(chatroom.getChatroomMinTemp());
        dto.setChatroomCreatorId(chatroom.getChatroomCreatorId());
        dto.setChatroomType(chatroom.getChatroomType().toString()); // Enum 값을 문자열로 변환하여 설정
        return dto;
    }

    private ChatEntranceDto convertToChatEntranceDto(ChatEntrance chatentrance) {
        ChatEntranceDto dto = new ChatEntranceDto();
        // 변환코드
        return dto;
    }
}
